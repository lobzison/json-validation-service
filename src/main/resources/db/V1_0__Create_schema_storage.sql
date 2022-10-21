create table json_schema(
    pk_id integer primary key autoincrement,
    schema_id text not null unique,
    body text not null
);