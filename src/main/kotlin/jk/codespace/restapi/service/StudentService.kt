package jk.codespace.restapi.service

import jk.codespace.restapi.dto.StudentDTO
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class StudentService {
    private val log = KotlinLogging.logger {}

    fun getStudent(studentId: String) : StudentDTO {
        log.info {"Attempting to get student :  $studentId" }
        return StudentDTO(
            studentId = studentId,
        firstName = "Joe",
        lastName = "Bloggs")
    }

    fun createStudent(student: StudentDTO): StudentDTO {
        log.info { "Attempting to create Student with Id : ${student.studentId}" }
        return student
    }

    fun updateStudent(student: StudentDTO): StudentDTO {
        log.info { "Attempting to update Student with Id : ${student.studentId}" }
        return student
    }

    fun deleteStudent(studentId: String): Boolean {
        log.info { "Attempting to delete Student with Id : ${studentId}" }
        return true
    }
}