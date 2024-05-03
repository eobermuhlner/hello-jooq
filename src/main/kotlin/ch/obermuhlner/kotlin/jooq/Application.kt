package ch.obermuhlner.kotlin.jooq

import org.example.jooq.public_.Tables.EMPLOYEE
import org.example.jooq.public_.tables.Employee
import org.example.jooq.public_.tables.records.EmployeeRecord
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.math.BigDecimal
import java.sql.DriverManager

fun main() {
    val connection = DriverManager.getConnection("jdbc:h2:file:~/hello-jooq-db;DB_CLOSE_DELAY=-1", "sa", "")
    val dsl: DSLContext = DSL.using(connection)

    val count = countEmployees(dsl)

    val exampleIndex = count + 1
    newEmployee(dsl, "Eric-$exampleIndex", 20 + exampleIndex, BigDecimal.valueOf(100000.0 + exampleIndex*10000))

    println(fetchAllEmployeeRecords(dsl))
}

fun countEmployees(dsl: DSLContext): Int {
    return dsl.selectCount().from(EMPLOYEE).fetchOne(0, Int::class.java) ?: 0
}

fun newEmployee(dsl: DSLContext, name: String, age: Int, salary: BigDecimal) {
    val newExample = dsl.newRecord(EMPLOYEE)
    newExample.name = name
    newExample.age = age
    newExample.salary = salary
    newExample.store()
}

fun fetchAllEmployeeRecords(dsl: DSLContext): org.jooq.Result<EmployeeRecord> {
    return dsl.selectFrom(EMPLOYEE).fetch()
}

