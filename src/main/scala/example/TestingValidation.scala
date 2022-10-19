package example

import cats.effect.IO
import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}

object TestingValidation {

  def printResults: IO[Unit] = IO {
    val factory: JsonSchemaFactory = JsonSchemaFactory.byDefault

    val fstabSchema: JsonNode =
      JsonLoader
        .fromString("""{
            |    "$schema": "http://json-schema.org/draft-04/schema#",
            |    "title": "/etc/fstab",
            |    "description": "JSON representation of /etc/fstab",
            |    "type": "object",
            |    "properties": {
            |        "swap": {
            |            "$ref": "#/definitions/mntent"
            |        }
            |    },
            |    "patternProperties": {
            |        "^/([^/]+(/[^/]+)*)?$": {
            |            "$ref": "#/definitions/mntent"
            |        }
            |    },
            |    "required": [ "/", "swap" ],
            |    "additionalProperties": false,
            |    "definitions": {
            |        "mntent": {
            |            "title": "mntent",
            |            "description": "An fstab entry",
            |            "type": "object",
            |            "properties": {
            |                "device": {
            |                    "type": "string"
            |                },
            |                "fstype": {
            |                    "type": "string"
            |                },
            |                "options": {
            |                    "type": "array",
            |                    "minItems": 1,
            |                    "items": { "type": "string" }
            |                },
            |                "dump": {
            |                    "type": "integer",
            |                    "minimum": 0
            |                },
            |                "fsck": {
            |                    "type": "integer",
            |                    "minimum": 0
            |                }
            |            },
            |            "required": [ "device", "fstype" ],
            |            "additionalItems": false
            |        }
            |    }
            |}""".stripMargin)

    val good: JsonNode =
      JsonLoader
        .fromString("""
            |{
            |    "/": {
            |        "device": "/dev/sda1",
            |        "fstype": "btrfs",
            |        "options": [ "ssd" ]
            |    },
            |    "swap": {
            |        "device": "/dev/sda2",
            |        "fstype": "swap"
            |    },
            |    "/tmp": {
            |        "device": "tmpfs",
            |        "fstype": "tmpfs",
            |        "options": [ "size=64M" ]
            |    },
            |    "/var/lib/mysql": {
            |        "device": "/dev/data/mysql",
            |        "fstype": "btrfs"
            |    }
            |}""".stripMargin)

    val bad: JsonNode = JsonLoader
      .fromString("""{
          |    "/": {
          |        "fstype": "btrfs",
          |        "options": [ "ssd" ]
          |    },
          |    "/tmp": {
          |        "device": "tmpfs",
          |        "fstype": "tmpfs",
          |        "options": [ "size=64M" ]
          |    },
          |    "/var/lib/mysql": {
          |        "device": "/dev/data/mysql",
          |        "fstype": "btrfs"
          |    }
          |}""".stripMargin)

    val bad2: JsonNode =
      JsonLoader
        .fromString("""{
            |    "/": {
            |        "device": "/dev/sda1",
            |        "options": [ "ssd" ]
            |    },
            |    "swap": {
            |        "device": "/dev/sda2",
            |        "fstype": "swap"
            |    },
            |    "/tmp": {
            |        "device": "tmpfs",
            |        "fstype": "tmpfs",
            |        "options": "size=64M"
            |    },
            |    "/var/lib/mysql": {
            |        "device": "/dev/data/mysql",
            |        "fstype": "btrfs"
            |    }
            |}""".stripMargin)

    val schema: JsonSchema = factory.getJsonSchema(fstabSchema)

    val a: ProcessingReport = schema.validate(good)
    val b                   = schema.validate(bad)
    val c                   = schema.validate(bad2)

    println(a)
    a.forEach(println)
    println(b.iterator().next())
    b.forEach(println)
    println(c)
  }
}
