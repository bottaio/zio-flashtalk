package pl.touk.zio.files.infractructure.ops

import java.nio.charset.StandardCharsets

import pl.touk.zio.files.infractructure.GroupedFiles
import scalaz.zio.ZIO

trait GroupedFilesOps {
  def postToService(bin: String, name: String, content: Array[Byte]): ZIO[GroupedFiles, Throwable, Unit] = ZIO.accessM { ctx =>
    ctx.binFiles.post(bin, name, content)
  }

  def fetchNames(bin: String): ZIO[GroupedFiles, Throwable, List[String]] = ZIO.accessM { ctx =>
    ctx.binFiles.list(bin)
  }

  def fetchContent(bin: String, name: String): ZIO[GroupedFiles, Throwable, String] = ZIO.accessM { ctx =>
    ctx.binFiles.get(bin, name)
      .map(_.utf8string)
  }

  def fetchContents(bin: String, names: List[String]): ZIO[GroupedFiles, Throwable, List[String]] = ZIO.accessM { ctx =>
    import scalaz._
    import Scalaz._
    import scalaz.zio.interop.scalaz72._

    names.map(fetchContent(bin, _).provide(ctx)).sequence
  }

  private implicit class ByteArraySyntax(self: Array[Byte]) {
    def utf8string: String = new String(self, StandardCharsets.UTF_8)
  }
}
