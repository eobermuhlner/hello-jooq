package ch.obermuhlner.kotlin.jooq

import org.example.jooq.public_.Tables.DEPARTMENT
import org.example.jooq.public_.Tables.EMPLOYEE
import org.example.jooq.public_.tables.pojos.Department
import org.example.jooq.public_.tables.pojos.Employee
import org.example.jooq.public_.tables.records.DepartmentRecord
import org.example.jooq.public_.tables.records.EmployeeRecord
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.math.BigDecimal
import java.sql.DriverManager

fun main() {
    val connection = DriverManager.getConnection("jdbc:h2:file:~/hello-jooq-db;DB_CLOSE_DELAY=-1", "sa", "")
    val dsl: DSLContext = DSL.using(connection)

    val countDepartments = countDepartments(dsl)
    val countEmployees = countEmployees(dsl)

    val departmentIndex = countDepartments + 1
    val departmentId = newDepartment_returningId(dsl, "Department-$departmentIndex")

    val departmentRecord = newDepartment_returningRecord(dsl, "DepartmentRecord-$departmentIndex")
    val departmentPojo = newDepartment_returningPojo(dsl, "DepartmentPojo-$departmentIndex")
    println(departmentRecord)
    println(departmentPojo)

    val employeeIndex = countEmployees + 1
    newEmployee(dsl, "Eric-$employeeIndex", 20 + employeeIndex, BigDecimal.valueOf(100000.0 + employeeIndex*10000), departmentId)

    println(fetchAllDepartments(dsl))
    println("Total salary: " + fetchDepartmentTotalSalary(dsl))
    println("Department Statistics: " + fetchDepartmentStatistics(dsl))

    println(fetchAllEmployeeRecords(dsl))
    println(fetchAllEmployees(dsl))
}

fun countEmployees(dsl: DSLContext): Int {
    return dsl.selectCount().from(EMPLOYEE).fetchOne()?.value1() ?: 0
}

fun countDepartments(dsl: DSLContext): Int {
    return dsl.selectCount().from(DEPARTMENT).fetchOne(0, Int::class.java) ?: 0
}

fun newDepartment_Simple(dsl: DSLContext, name: String) {
    dsl.insertInto(DEPARTMENT, DEPARTMENT.NAME)
        .values(name)
        .execute()
}

fun newDepartment_returningId(dsl: DSLContext, name: String): Int {
    return dsl.insertInto(DEPARTMENT, DEPARTMENT.NAME)
        .values(name)
        .returningResult(DEPARTMENT.ID)
        .fetchOne()!!
        .get(DEPARTMENT.ID)
}

fun newDepartment_returningRecord(dsl: DSLContext, name: String): DepartmentRecord {
    return dsl.insertInto(DEPARTMENT, DEPARTMENT.NAME)
        .values(name)
        .returning()
        .fetchOne()!!
}

fun newDepartment_returningPojo(dsl: DSLContext, name: String): Department {
    return dsl.insertInto(DEPARTMENT, DEPARTMENT.NAME)
        .values(name)
        .returning()
        .fetchInto(Department::class.java)[0]
}


fun newEmployee(dsl: DSLContext, name: String, age: Int, salary: BigDecimal, departmentId: Int? = null) {
    val newEmployee = dsl.newRecord(EMPLOYEE)
    newEmployee.name = name
    newEmployee.age = age
    newEmployee.salary = salary
    newEmployee.departmentId = departmentId
    newEmployee.store()
}

fun fetchAllEmployeeRecords(dsl: DSLContext): org.jooq.Result<EmployeeRecord> {
    return dsl.selectFrom(EMPLOYEE).fetch()
}

fun fetchAllEmployees(dsl: DSLContext): List<Employee> {
    return dsl.selectFrom(EMPLOYEE).fetchInto(Employee::class.java)
}

fun fetchAllDepartments(dsl: DSLContext): List<Department> {
    return dsl.selectFrom(DEPARTMENT).fetchInto(Department::class.java)
}

data class DepartmentStatistics(val name: String, val totalSalary: BigDecimal, val avgSalary: BigDecimal, val avgAge: BigDecimal)
fun fetchDepartmentStatistics(dsl: DSLContext): List<DepartmentStatistics> {
    return dsl.select(
        DEPARTMENT.NAME,
        DSL.sum(EMPLOYEE.SALARY).`as`("totalSalary"),
        DSL.avg(EMPLOYEE.SALARY).`as`("avgSalary"),
        DSL.avg(EMPLOYEE.AGE).`as`("avgAge")
    )
        .from(EMPLOYEE)
        .join(DEPARTMENT).on(EMPLOYEE.DEPARTMENT_ID.eq(DEPARTMENT.ID))
        .groupBy(DEPARTMENT.NAME)
        .fetchInto(DepartmentStatistics::class.java)
}

fun fetchDepartmentTotalSalary(dsl: DSLContext): Map<String, BigDecimal> {
    return dsl.select(DEPARTMENT.NAME, DSL.sum(EMPLOYEE.SALARY))
        .from(EMPLOYEE)
        .join(DEPARTMENT).on(EMPLOYEE.DEPARTMENT_ID.eq(DEPARTMENT.ID))
        .groupBy(DEPARTMENT.NAME)
        .fetchMap(DEPARTMENT.NAME, DSL.sum(EMPLOYEE.SALARY))
}