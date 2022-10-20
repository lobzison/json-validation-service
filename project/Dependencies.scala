import sbt._

object Dependencies {
  lazy val munit               = "org.typelevel"               %% "munit-cats-effect-3"   % "1.0.7"
  lazy val ce                  = "org.typelevel"               %% "cats-effect"           % "3.3.14"
  lazy val jsonSchemaValidator = "com.github.java-json-tools"   % "json-schema-validator" % "2.2.14"
  lazy val tapir               = "com.softwaremill.sttp.tapir" %% "tapir-core"            % "1.1.3"
  lazy val circe               = "io.circe"                    %% "circe-core"            % "0.14.3"
//  lazy val http4s = ???
//  lazy val tapirHttp4s = ???
//  lazy val circeHttp4s = ???
//  lazy val circeJackson = ???

}
