import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    id("nu.studer.jooq") version "7.0"
    id("org.flywaydb.flyway") version "9.0.0"
    application
}

group = "ch.obermuhlner"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val h2Version = "2.2.224"
val jooqVersion = "3.19.1"
val jdbcUrl = "jdbc:h2:file:~/hello-jooq-db;DB_CLOSE_DELAY=-1"
val jdbcUser = "sa"
val jdbcPassword = ""

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jooq:jooq:$jooqVersion")
    implementation("com.h2database:h2:$h2Version")
    jooqGenerator("com.h2database:h2:$h2Version")
    runtimeOnly("org.slf4j:slf4j-simple:1.7.32")
    testImplementation(kotlin("test"))
}

flyway {
    url = jdbcUrl
    user = jdbcUser
    password = jdbcPassword
    locations = arrayOf("filesystem:src/main/resources/db/migration")
}

application {
    mainClass.set("ch.obermuhlner.kotlin.jooq.ApplicationKt")
}

jooq {
    version.set(jooqVersion)
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)
    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(true)
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.h2.Driver"
                    url = jdbcUrl
                    user = jdbcUser
                    password = jdbcPassword
                }
                generator.apply {
                    database.apply {
                        name = "org.jooq.meta.h2.H2Database"
                        includes = ".*"
                    }
                    target.apply {
                        packageName = "org.example.jooq"
                        directory = "src/main/java"
                    }
                }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
