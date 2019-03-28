package pl.touk.zio.files.infractructure.ops

import scalaz.zio.ZIO
import scalaz.zio.console.{Console, getStrLn, putStrLn}

trait ConsoleOps {
  def getBinName: ZIO[Console, Throwable, String] = askForString("Bin?")

  def getFileName: ZIO[Console, Throwable, String] = askForString("Name?")

  def getBinAndFileNames: ZIO[Console, Throwable, (String, String)] = getBinName zip getFileName

  def getFileContent: ZIO[Console, Throwable, Array[Byte]] = askForString("Content?").map(_.getBytes)

  private def askForString(prompt: String): ZIO[Console, Throwable, String] = for {
    _   <- putStrLn(prompt)
    name <- getStrLn
  } yield name
}
