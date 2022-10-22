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

## Design challenges
Error handling is challenging. 
The issue is that API have the same model of "error" for JSON that is not validating against the schema(expected outcome),
and "invalid json"/"schema not found"/"duplicate name"(unexpected result). That leads to the problems in design, where the same error
could exist in both success and failure channels.

The problem gets worse, since we are expected to return "schema" and "action" in the error response, even for things such as invalid JSON.
Normally we could just have two separate classes with the same set of fields, since
That leads to leaking details between layers. The value of having them in the response is dubious, since client already knows what endpoint was called, and the identified passed.

### Solution suggestion
Separate negative validation outcome from all other errors. Negative validation outcome is not an error, but a positive outcome, since without it the service would be useless.
Errors such as "invalid json"/"schema not found"/"duplicate name" return correct HTTP code, and a valid json as a body, that contains only message with some details.
That information should be enough for a client to figure out what's wrong, while the error handling would be much cleaner.
