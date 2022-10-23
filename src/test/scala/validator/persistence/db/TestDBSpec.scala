package validator.persistence.db

import cats.effect.IO
import cats.effect.std.UUIDGen
import doobie.implicits._
import munit.CatsEffectSuite

import java.nio.file.{Files, Paths}
import validator.Fixtures._
import validator.model.{JsonSchemaRaw, SchemaId}

class TestDBSpec extends CatsEffectSuite {
  test("Must create and delete a DB") {

    def notExists(name: String) = IO(!Files.exists(Paths.get(name)))

    for {
      id <- UUIDGen.randomUUID[IO]
      _ <- TestDB.oneTimeDb(Some(id)).use { xa =>
        val result = fr"select 1".query[Int].option.transact(xa)
        assertIO(result, Some(1))
      }
      _ <- assertIOBoolean(notExists(TestDB.fileName(id.toString)))
    } yield ()

  }

  val select = fr"select schema_id, body from json_schema".query[(SchemaId, JsonSchemaRaw)].to[List]

  test("Must apply a migration, insert and get data back") {
    TestDB.oneTimeDb().use { xa =>
      val insert =
        fr"insert into json_schema(schema_id, body) values ($testSchemaId1, $testSchemaRaw1)".update.run
      val result = for { // insert and select are separate transactions
        _   <- insert.transact(xa)
        res <- select.transact(xa)
      } yield res
      assertIO(result, List((testSchemaId1, testSchemaRaw1)))
    }
  }

  test("Must not preserve results between runs") {
    TestDB.oneTimeDb().use { xa =>
      assertIO(select.transact(xa), List.empty)
    }
  }
}
