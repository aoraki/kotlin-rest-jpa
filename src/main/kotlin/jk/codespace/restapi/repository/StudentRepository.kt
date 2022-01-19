package jk.codespace.restapi.repository

import jk.codespace.restapi.entities.Student
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository : CrudRepository<Student, Long> {
    fun findByStudentId(studentId : String): Student?
}