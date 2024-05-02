package ch.obermuhlner.kotlin.jooq

import org.example.jooq.public_.Tables
import org.example.jooq.public_.Tables.EXAMPLE
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.sql.DriverManager

fun main() {
    val connection = DriverManager.getConnection("jdbc:h2:file:~/hello-jooq-db;DB_CLOSE_DELAY=-1", "sa", "")
    val dsl: DSLContext = DSL.using(connection)

    val count = dsl.selectCount().from(EXAMPLE).fetchOne(0, Int::class.java) ?: 0
    val exampleIndex = count + 1

    val newExample = dsl.newRecord(EXAMPLE)
    newExample.name = "Eric-$exampleIndex"
    newExample.store()

    // Fetching data
    val result = dsl.selectFrom(EXAMPLE).fetch()
    println(result)
}

