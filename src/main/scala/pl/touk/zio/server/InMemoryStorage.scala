package pl.touk.zio.server

import java.util.concurrent.ConcurrentHashMap

import cats.effect.IO

object InMemoryStorage {
  private val binNameSeparator = "/"
  private val extractNameRegexp = ".*/(.*)".r
  private val map = new ConcurrentHashMap[String, Array[Byte]]()

  def list(bin: String): IO[List[String]] = IO {
    import scala.collection.JavaConverters._

    map.asScala.collect {
      case (extractNameRegexp(name), _) => name
    }.toList
  }

  def get(bin: String, name: String): IO[Array[Byte]] = IO(map.get(mkKey(bin, name)))

  def put(bin: String, name: String, data: Array[Byte]) = IO(map.put(mkKey(bin, name), data))

  private def mkKey(bin: String, name: String): String  = s"$bin$binNameSeparator$name"
}
