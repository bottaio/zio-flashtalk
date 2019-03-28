import java.util.concurrent.{Executor, Executors}

import org.scalatest.{FunSuite, Matchers}
import scalaz.zio.{DefaultRuntime, Fiber, Ref}
import scalaz.zio._
import scalaz.zio.internal.{Platform, PlatformLive}

import scala.concurrent.ExecutionContext

object RTS extends DefaultRuntime {
  override val Platform: Platform = PlatformLive.fromExecutionContext(
    ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor()))
}

class FiberTest extends FunSuite with Matchers {
  test("can produce nothing") {
    val fiber = Fiber.unit

    RTS.unsafeRun {
      fiber.join.map { v => v shouldBe () }
    }
  }

  test("can produce a single value") {
    val fiber = Fiber.succeedLazy(100)

    RTS.unsafeRun {
      fiber.join.map { v => v shouldBe 100 }
    }
  }

  test("can produce a many values") {
    val fiber = Fiber.succeedLazy(100 :: 101 :: Nil)

    RTS.unsafeRun {
      fiber.join.map(_ shouldBe 100 :: 101 :: Nil)
    }
  }

  test("can await other fibers") {
    val mem = Ref.make(0)

    val semaphores = (0 until 50).map(i => Semaphore.make(if (i == 0) 0 else 1))
    def terminate(sem: List[Semaphore]): UIO[Unit] = {
      ZIO.collectAll(sem.map(_.release)).const(())
    }

    def continue(ref: Ref[Int], v: Int, mySem: Semaphore, nextSem: Semaphore, stop: () => UIO[Unit]): ZIO[Any, Nothing, Unit] = for {
      _ <- ref.set(v + 1)
      _ <- nextSem.release
      _ <- fiber(ref, mySem, nextSem, stop)
    } yield ()

    def fiber(ref: Ref[Int], mySem: Semaphore, nextSem: Semaphore, stop: () => UIO[Unit]): ZIO[Any, Nothing, Unit] = for {
      _ <- mySem.acquire
      v <- ref.get
      _ <- if (v == 1000000) stop() else continue(ref, v, mySem, nextSem, stop)
    } yield ()

    val program = for {
      ref    <- mem
      sems   <- ZIO.collectAll(semaphores)
      nexts   = sems.drop(1) :+ sems.head
      stop    = () => terminate(sems)
      fibers <- ZIO.collectAll(sems.zip(nexts).map { case (sem, next) => fiber(ref, sem, next, stop).fork })
      _      <- ZIO.collectAll(fibers.map(_.join))
      v      <- ref.get
    } yield v

    val start = System.currentTimeMillis()
    RTS.unsafeRun {
      program.map(_ shouldBe 1000000)
    }

    println(s"Took: ${System.currentTimeMillis() - start} ms")
  }


}
