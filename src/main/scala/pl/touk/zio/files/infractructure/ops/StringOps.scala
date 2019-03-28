package pl.touk.zio.files.infractructure.ops

import scalaz.zio.{UIO, ZIO}

trait StringOps {
  def concat(contents: List[String]): UIO[String] = ZIO.succeedLazy {
    contents.mkString("\n")
  }
}
