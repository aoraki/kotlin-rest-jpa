package jk.codespace.restapi.service

import jk.codespace.restapi.dto.Student
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class StudentService {
    private val log = KotlinLogging.logger {}

    fun getStudent(studentId: String) : Student {
        log.info {"Attempting to get student :  $studentId" }
        return Student(
            studentId = studentId,
        firstName = "Joe",
        lastName = "Bloggs")
    }

    fun createStudent(student: Student): Student {
        log.info { "Attempting to create Student with Id : ${student.studentId}" }
        return student
    }

}