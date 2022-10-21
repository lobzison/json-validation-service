create table json_schema(
    pk_id INTEGER PRIMARY KEY AUTOINCREMENT,
    schema_id text not null unique,
    body text not null
);