package pl.touk.zio.files

import pl.touk.zio.files.infractructure.GroupedFiles
import scalaz.zio._
import scalaz.zio.console._

object CopySingleConsole extends App {
  import pl.touk.zio.files.infractructure.ops._

  override def run(args: List[String]): ZIO[CopySingleConsole.Environment, Nothing, Int] = {
    program.fold(_ => 1, _ => 0)
  }

  private object Context extends Console.Live with GroupedFiles.Live

  private val rawProgram: ZIO[GroupedFiles with Console, Throwable, Unit] = for {
    bin     <- getBinName
    name    <- getFileName
    content <- getFileContent
    _       <- postToService(bin, name, content)
  } yield ()

  private val program: IO[Throwable, Unit] = {
    rawProgram.provide(Context)
  }
}

// todo: decide what to live code (probably only wiring together last part?
// todo: raise exception and see how shitty it is