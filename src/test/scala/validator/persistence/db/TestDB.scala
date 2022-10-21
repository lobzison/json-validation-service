package validator.persistence.db
import cats.effect.std.UUIDGen
import cats.effect.{IO, Resource}
import doobie._
import doobie.hikari.HikariTransactor
import fly4s.core._
import fly4s.core.data._

import java.nio.file.{Files, Paths}
import java.util.UUID

object TestDB {

  private val driver          = "org.sqlite.JDBC"
  private val user            = ""
  private val password        = ""
  def fileName(name: UUID)    = s"$name.db"
  private def url(name: UUID) = s"jdbc:sqlite:${fileName(name)}"
  private def cleanUpDBOnClose(name: UUID) = Resource.make(IO(fileName(name))) { name =>
    for {
      filePath <- IO(Paths.get(name))
      res      <- IO(Files.deleteIfExists(filePath))
    } yield res
  }

  def fly4sRes(name: UUID): Resource[IO, Fly4s[IO]] = Fly4s.make[IO](
    url = url(name),
    user = None,
    password = None,
    config = Fly4sConfig(
      table = "flyway",
      locations = Location.of("db")
    )
  )

  def oneTimeDb(name: Option[UUID] = None): Resource[IO, Transactor[IO]] =
    for {
      dbNameGenerated <- Resource.eval(UUIDGen.randomUUID[IO])
      dbName = name.getOrElse(dbNameGenerated)
      // resource cleanup is reversed of acquisition
      // in order to delete db file after flyway and transactor
      // finalizers are run - it has to be "acquired" before
      _  <- cleanUpDBOnClose(dbName)
      ec <- ExecutionContexts.fixedThreadPool[IO](1)
      res <- HikariTransactor.newHikariTransactor[IO](
        driver,
        url(dbName),
        user,
        password,
        ec
      )
      flyway <- fly4sRes(dbName)
      _      <- Resource.eval(flyway.migrate)

    } yield res

}
