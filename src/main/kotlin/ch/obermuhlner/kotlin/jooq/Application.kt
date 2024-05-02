package ch.obermuhlner.kotlin.jooq

import org.example.jooq.public_.Tables
import org.example.jooq.public_.Tables.EXAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.sql.DriverManager

fun main() {
    val connection = DriverManager.getConnection("jdbc:h2:file:~/hello-jooq-db;DB_CLOSE_DELAY=-1", "sa", "")
    val dsl: DSLContext = DSL.using(connection)

    val newExample = dsl.newRecord(Tables.EXAMPLE)
    newExample.name = "Eric"
    newExample.store()

    // Fetching data
    val result = dsl.selectFrom(EXAMPLE).fetch()
    println(result)
}

