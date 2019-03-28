package pl.touk.zio.files

import pl.touk.zio.files.infractructure.GroupedFiles
import scalaz.zio.console.Console
import scalaz.zio.{App, IO, ZIO}

object CopyMultipleConsole extends App {
  import pl.touk.zio.files.infractructure.ops._

  override def run(args: List[String]): ZIO[CopyMultipleConsole.Environment, Nothing, Int] = {
    program.fold(_ => 1, _ => 0)
  }

  private object Context extends Console.Live with GroupedFiles.Live

  private val rawProgram: ZIO[GroupedFiles with Console, Throwable, Unit] = for {
    bin           <- getBinName
    names         <- fetchNames(bin)
    contents      <- fetchContents(bin, names)
    finalContent  <- concat(contents)
    contentAsBytes = finalContent.getBytes
    binAndName    <- getBinAndFileNames
    (bin, name)    = binAndName
    _             <- postToService(bin, name, contentAsBytes)
  } yield ()

  private val program: IO[Throwable, Unit] = {
    rawProgram.provide(Context)
  }
}
