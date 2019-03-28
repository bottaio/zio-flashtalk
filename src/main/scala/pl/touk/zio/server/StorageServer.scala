package pl.touk.zio.server


import cats.effect.IOApp
import org.http4s.HttpRoutes

object StorageServer extends IOApp {
  import cats.effect._
  import cats.implicits._
  import io.circe.syntax._
  import org.http4s.dsl.io._
  import org.http4s.server.blaze._

  private val storageService = InMemoryStorage

  private val service = HttpRoutes.of[IO] {
    case _   @ GET  -> Root / bin        => for {
      names    <- storageService.list(bin)
      namesJson = names.asJson.noSpaces
      result   <- Ok(namesJson)
    } yield result

    case _   @ GET  -> Root / bin / name => for {
      content <- storageService.get(bin, name)
      result  <- Ok(content)
    } yield result

    case req @ POST -> Root / bin / name => for {
      data   <- req.as[Array[Byte]]
      _      <- storageService.put(bin, name, data)
      result <- Ok()
    } yield result
  }.orNotFound

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO]
      .bindHttp(8089, "localhost")
      .withHttpApp(service)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
