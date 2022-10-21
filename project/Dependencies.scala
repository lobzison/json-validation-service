import sbt._

object Dependencies {
  lazy val munit = "org.typelevel"               %% "munit-cats-effect-3" % "1.0.7"
  lazy val ce    = "org.typelevel"               %% "cats-effect"         % "3.3.14"
  lazy val tapir = "com.softwaremill.sttp.tapir" %% "tapir-core"          % "1.1.3"

//  lazy val http4s = ???
//  lazy val tapirHttp4s = ???
//  lazy val circeHttp4s = ???

  object Database {
    lazy val sqlite       = "org.xerial"          % "sqlite-jdbc"   % "3.39.3.0"
    lazy val doobieCore   = "org.tpolecat"       %% "doobie-core"   % "1.0.0-RC2"
    lazy val doobieHikari = "org.tpolecat"       %% "doobie-hikari" % "1.0.0-RC2"
    lazy val flyway       = "com.github.geirolz" %% "fly4s-core"    % "0.0.14"
  }

  object Json {
    lazy val jsonSchemaValidator = "com.github.java-json-tools" % "json-schema-validator" % "2.2.14"
    lazy val circe               = "io.circe"                  %% "circe-core"            % "0.14.3"
    lazy val circeParser         = "io.circe"                  %% "circe-parser"          % "0.14.3"
    lazy val circeJackson        = "io.circe"                  %% "circe-jackson211"      % "0.14.0"
  }
}
