package jk.codespace.restapi.controller

import jk.codespace.restapi.dto.Student
import jk.codespace.restapi.service.StudentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class StudentController(
    private val studentService: StudentService
) {
    @GetMapping("v1/students/{studentid}")
    fun getStudentById(@PathVariable("studentid") studentId: String): ResponseEntity<Student> {
        val student = studentService.getStudent(studentId = studentId)
        return ResponseEntity(student, HttpStatus.OK)
    }
}