package validator.model.config

case class DBConfig(
    name: String,
    user: String,
    password: String,
    flywayTable: String,
    migrationLocation: String
)
