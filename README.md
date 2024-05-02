Sample application for JOOQ and Flyway.

The database is file based embedded H2.

Tested with Java 11.

```shell
./gradlew flywayMigrate

./gradlew --stop

./gradlew generateJooq

./gradlew run
```
