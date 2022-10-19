import sbt._

object Dependencies {
  lazy val munit               = "org.typelevel"             %% "munit-cats-effect-3"   % "1.0.7"
  lazy val ce                  = "org.typelevel"             %% "cats-effect"           % "3.3.14"
  lazy val jsonSchemaValidator = "com.github.java-json-tools" % "json-schema-validator" % "2.2.14"
}
