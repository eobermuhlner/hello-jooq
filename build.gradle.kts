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

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jooq:jooq:3.19.1") // Check for the latest version
    implementation("com.h2database:h2:2.2.224")
    jooqGenerator("com.h2database:h2:2.2.224")
    runtimeOnly("org.slf4j:slf4j-simple:1.7.32")
    testImplementation(kotlin("test"))
}

flyway {
    url = "jdbc:h2:file:~/hello-jooq-db;DB_CLOSE_DELAY=-1"
    user = "sa"
    password = ""
    locations = arrayOf("filesystem:src/main/resources/db/migration")
}

application {
    mainClass.set("ch.obermuhlner.kotlin.jooq.ApplicationKt")
}

tasks.register("printClasspath") {
    doLast {
        configurations.runtimeClasspath.get().forEach { println(it.absolutePath) }
    }
}

jooq {
    version.set("3.16.5") // Ensure it matches the JOOQ library version
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)
    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(true)
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.h2.Driver"
                    url = "jdbc:h2:file:~/hello-jooq-db;DB_CLOSE_DELAY=-1"
                    user = "sa"
                    password = ""
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
