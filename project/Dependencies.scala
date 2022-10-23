import sbt._

object Dependencies {

  lazy val munit = "org.typelevel" %% "munit-cats-effect-3" % "1.0.7"
  lazy val ce    = "org.typelevel" %% "cats-effect"         % "3.3.14"

  object Database {
    lazy val sqlite       = "org.xerial"          % "sqlite-jdbc"   % "3.39.3.0"
    lazy val doobieCore   = "org.tpolecat"       %% "doobie-core"   % Versions.doobieVersion
    lazy val doobieHikari = "org.tpolecat"       %% "doobie-hikari" % Versions.doobieVersion
    lazy val flyway       = "com.github.geirolz" %% "fly4s-core"    % "0.0.14"
  }

  object Json {
    lazy val jsonSchemaValidator = "com.github.java-json-tools" % "json-schema-validator" % "2.2.14"
    lazy val circe           = "io.circe"     %% "circe-core"       % Versions.circeVersion
    lazy val circeParser     = "io.circe"     %% "circe-parser"     % Versions.circeVersion
    lazy val circeJackson    = "io.circe"     %% "circe-jackson211" % "0.14.0"
    lazy val enumeratumCirce = "com.beachape" %% "enumeratum-circe" % "1.7.0"
  }

  object Http {
    lazy val tapir = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % Versions.tapirVersion
    lazy val http4sBazeServer = "org.http4s" %% "http4s-blaze-server" % "0.23.12"
    lazy val tapirOpenApi =
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % Versions.tapirVersion
    lazy val tapirCirce =
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % Versions.tapirVersion
  }

  object Versions {
    lazy val doobieVersion = "1.0.0-RC2"
    lazy val circeVersion  = "0.14.3"
    lazy val tapirVersion  = "1.1.3"
  }

  lazy val httpDependencies = Seq(
    Http.tapir,
    Http.http4sBazeServer,
    Http.tapirCirce,
    Http.tapirOpenApi
  )

  lazy val dbDependencies = Seq(
    Database.sqlite,
    Database.doobieCore,
    Database.doobieHikari,
    Database.flyway
  )

  lazy val jsonDependencies = Seq(
    Json.jsonSchemaValidator,
    Json.circe,
    Json.circeParser,
    Json.circeJackson,
    Json.enumeratumCirce
  )

}
