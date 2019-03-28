package pl.touk.zio.files.infractructure

import scalaz.zio.{Task, ZIO}

trait GroupedFiles {
  val binFiles: GroupedFiles.Service
}

object GroupedFiles {
  trait Service {
    def post(bin: String, name: String, content: Array[Byte]): Task[Unit]
    def list(bin: String): Task[List[String]]
    def get(bin: String, name: String): Task[Array[Byte]]
  }

  trait Live extends GroupedFiles {
    override val binFiles: Service = new Service {
      private val serviceUrl = "http://localhost:8089"

      override def post(bin: String, name: String, content: Array[Byte]): Task[Unit] = ZIO.succeedLazy {
        requests.post(s"$serviceUrl/$bin/$name", data = content)
      }

      override def list(bin: String): Task[List[String]] = ZIO.succeedLazy {
        import io.circe.parser._
        parse(requests.get(s"$serviceUrl/$bin").text)
          .right.get
          .as[List[String]]
          .right.get
      }

      override def get(bin: String, name: String): Task[Array[Byte]] = ZIO.succeedLazy {
        requests.get(s"$serviceUrl/$bin/$name").text.getBytes
      }
    }
  }

  object Live extends Live
}