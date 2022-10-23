import sbt._

object Dependencies {

  lazy val ce    = "org.typelevel" %% "cats-effect"         % "3.3.14"
  lazy val munit = "org.typelevel" %% "munit-cats-effect-3" % "1.0.7"

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
    lazy val http4sEmberServer = "org.http4s" %% "http4s-ember-server" % Versions.httpVersion
    lazy val http4sDsl         = "org.http4s" %% "http4s-dsl"          % Versions.httpVersion
    lazy val http4sCirce       = "org.http4s" %% "http4s-circe"        % Versions.httpVersion
  }

  object Misc {
    lazy val pureconfigCats =
      "com.github.pureconfig" %% "pureconfig-cats-effect" % Versions.pureconfigVersion
    lazy val pureconfig = "com.github.pureconfig" %% "pureconfig"      % Versions.pureconfigVersion
    lazy val log4cats   = "org.typelevel"         %% "log4cats-slf4j"  % "2.5.0"
    lazy val logback    = "ch.qos.logback"         % "logback-classic" % "1.4.4"
  }

  object Versions {
    lazy val doobieVersion     = "1.0.0-RC2"
    lazy val circeVersion      = "0.14.3"
    lazy val httpVersion       = "1.0.0-M37"
    lazy val pureconfigVersion = "0.17.1"
  }

  lazy val httpDependencies = Seq(
    Http.http4sEmberServer,
    Http.http4sDsl,
    Http.http4sCirce
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

  lazy val miscDependencies = Seq(
    Misc.log4cats,
    Misc.logback,
    Misc.pureconfig,
    Misc.pureconfigCats
  )

}
