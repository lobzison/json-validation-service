import Dependencies._

scalaVersion := "2.13.10"
version      := "0.1.0"

lazy val dbDependencies = Seq(
  Database.sqlite,
  Database.doobieCore,
  Database.doobieHikari,
  Database.flyway
)

lazy val root = (project in file("."))
  .settings(
    name := "json-validation-service",
    libraryDependencies := Seq(
      ce,
      jsonSchemaValidator,
      tapir,
      circe,
      circeParser,
      munit % Test
    ) ++ dbDependencies
  )
