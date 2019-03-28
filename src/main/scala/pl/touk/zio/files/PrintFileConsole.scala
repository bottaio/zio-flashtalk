package pl.touk.zio.files

import pl.touk.zio.files.infractructure.GroupedFiles
import scalaz.zio.console.{Console, putStrLn}
import scalaz.zio.{App, IO, ZIO}

object PrintFileConsole extends App {
  import pl.touk.zio.files.infractructure.ops._

  override def run(args: List[String]): ZIO[CopySingleConsole.Environment, Nothing, Int] = {
    program.fold(_ => 1, _ => 0)
  }

  private object Context extends Console.Live with GroupedFiles.Live

  private val rawProgram: ZIO[GroupedFiles with Console, Throwable, Unit] = for {
    bin     <- getBinName
    name    <- getFileName
    content <- fetchContent(bin, name)
    _       <- putStrLn(content)
  } yield ()

  private val program: IO[Throwable, Unit] = {
    rawProgram.provide(Context)
  }
}
