# JSON validation service

## Requirements
Service requirements are specified over [here](https://gist.github.com/goodits/20818f6ded767bca465a7c674187223e)
There are no non-functional requirements present.

To fulfill the requirements service will need
1. Persistent storage
2. HTTP server
3. JSON serialization and validation against a schema

## Assumptions made
1. Relational database can handle the read/write load of the service
2. There can be multiple instances of the API server with a single write replica of database

## Stack
General library choice will default to Typelevel libraries, since they are both mentioned in the job description, and I'm the most familiar with them

### Storage
Since we are assuming that relational database can handle the load - this is what we are going for. 
Default choice would be PostgreSQL, but since it's an exercise - SQLite will be used. 
It has an advantage of easier setup, without usage of Docker. It can be swapped for PostgreSQL without much effort later.

### HTTP server
Tapir for endpoint description and documentation generation, http4s with Ember as a backend

### JSON
This is tricky. We are already committed to the validation library, and the library uses Jackson as it's json model.
But since we want to use Tapir for auto documentation - we need to use a supported library, and Jackson is not one of them.
The planned solution is to use circe and circe-jackson to convert between models.

