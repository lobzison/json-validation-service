package validator.persistence

import cats.effect.IO
import doobie.implicits._
import doobie.ConnectionIO
import munit.CatsEffectSuite
import validator.Fixtures._
import validator.model.{JsonSchemaRaw, SchemaId}
import validator.persistence.db.TestDB
import validator.model.errors.SchemaAlreadyExists

class SchemaPersistenceSpec extends CatsEffectSuite {

  private val persistenceRes =
    TestDB.oneTimeDb().map(xa => new SchemaPersistenceImpl[IO](xa))

  test("get must return None if nothing is found") {
    persistenceRes.use { persistence =>
      assertIO(persistence.get(testSchemaId1), None)
    }
  }
  test("get must return Some if there is a row") {

    TestDB.oneTimeDb().use { xa =>
      val persistence = new SchemaPersistenceImpl[IO](xa)
      val result = {
        for {
          _   <- insert(testSchemaId1, testSchemaRaw1).transact(xa)
          res <- persistence.get(testSchemaId1)
        } yield res
      }
      assertIO(result, Some(testSchemaRaw1))
    }
  }

  test("get must return the correct schema for id") {
    TestDB.oneTimeDb().use { xa =>
      val persistence = new SchemaPersistenceImpl[IO](xa)

      val result = {
        for {
          _   <- insert(testSchemaId1, testSchemaRaw1).transact(xa)
          _   <- insert(testSchemaId2, testSchemaRaw2).transact(xa)
          res <- persistence.get(testSchemaId2)
        } yield res
      }
      assertIO(result, Some(testSchemaRaw2))
    }
  }

  test("create must insert a row successfully if no constraints are violated") {
    persistenceRes.use { persistence =>
      val result = {
        for {
          _   <- persistence.create(testSchemaId1, testSchemaRaw1)
          res <- persistence.get(testSchemaId1)
        } yield res
      }
      assertIO(result, Some(testSchemaRaw1))
    }
  }

  test("create must fail to create schema with the same id") {
    persistenceRes.use { persistence =>
      val result = {
        for {
          _ <- persistence.create(testSchemaId1, testSchemaRaw1)
          _ <- persistence.create(testSchemaId1, testSchemaRaw2)
        } yield ()
      }
      interceptIO[SchemaAlreadyExists](result)
    }
  }

  test("create must create two schemas with different id's and same body") {
    persistenceRes.use { persistence =>
      val result = {
        for {
          _ <- persistence.create(testSchemaId1, testSchemaRaw1)
          _ <- persistence.create(testSchemaId2, testSchemaRaw1)
        } yield ()
      }
      assertIO_(result)
    }
  }

  private def insert(id: SchemaId, schema: JsonSchemaRaw): ConnectionIO[Int] =
    fr"insert into json_schema(schema_id, body) values ($id, $schema)".update.run
}
