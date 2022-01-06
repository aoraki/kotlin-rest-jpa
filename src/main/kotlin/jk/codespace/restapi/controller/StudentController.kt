package jk.codespace.restapi.controller

import jk.codespace.restapi.dto.Student
import jk.codespace.restapi.service.StudentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class StudentController(
    private val studentService: StudentService
) {

    @GetMapping("v1/students/{studentid}")
    fun getStudentById(@PathVariable("studentid") studentId: String): ResponseEntity<Student> {
        val student = studentService.getStudent(studentId = studentId)
        return ResponseEntity(student, HttpStatus.OK)
    }

    @PostMapping("v1/students")
    fun createStudent(@RequestBody student: Student): ResponseEntity<Student> {
        val createdStudent = studentService.createStudent(student = student)
        return ResponseEntity(createdStudent, HttpStatus.OK)
    }

}