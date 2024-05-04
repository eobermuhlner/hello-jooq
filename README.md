Sample application as a showcase for:
- kotlin
- JOOQ
- Flyway
- H2 embedded database (file based)

Tested with Java 11.

JOOQ is configured to generate immutable Java POJO classes.
The JOOQ generated classes are not committed to git.

# Installation

Clone the git repository.

Run the following to create the database `hello-jooq-db.mv.db` in your home directory.
```shell
./gradlew flywayMigrate
```

I have experienced troubles with the database file still being locked (leading to errors with the next steps).
In this case I recommend stopping the gradle daemon. This solved my problems.
```shell
./gradlew --stop
```

Generate the JOOQ classes:
```shell
./gradlew generateJooq
```

Run the sample application:
```shell
./gradlew run
```
