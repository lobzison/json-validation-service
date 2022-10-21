import Dependencies._

scalaVersion := "2.13.10"
version      := "0.1.0"

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
  Json.circeJackson
)

lazy val root = (project in file("."))
  .settings(
    name := "json-validation-service",
    libraryDependencies := Seq(
      ce,
      munit % Test
    ) ++ jsonDependencies
      ++ dbDependencies
  )
