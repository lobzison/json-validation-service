package validator.persistence.db
import cats.effect.{Async, Resource}
import cats.implicits._
import doobie.hikari.HikariTransactor
import doobie.{ExecutionContexts, Transactor}
import fly4s.core.Fly4s
import fly4s.core.data.{Fly4sConfig, Location}
import validator.model.config.DBConfig

class DBImpl[F[_]: Async](val config: DBConfig) {

  protected val driver = "org.sqlite.JDBC"
  protected val name   = config.name.pure[F]

  def fileName(name: String) = s"$name.db"

  protected def url(name: String) = s"jdbc:sqlite:${fileName(name)}"

  protected def fly4sRes(name: String): Resource[F, Fly4s[F]] = Fly4s.make[F](
    url = url(name),
    user = None,
    password = None,
    config = Fly4sConfig(
      table = "flyway",
      locations = Location.of("db")
    )
  )

  val transactor: Resource[F, Transactor[F]] =
    for {
      dbName <- Resource.eval(name)
      ec     <- ExecutionContexts.fixedThreadPool[F](1)
      res <- HikariTransactor.newHikariTransactor[F](
        driver,
        url(dbName),
        config.user,
        config.password,
        ec
      )
      flyway <- fly4sRes(dbName)
      _      <- Resource.eval(flyway.migrate)

    } yield res
}
