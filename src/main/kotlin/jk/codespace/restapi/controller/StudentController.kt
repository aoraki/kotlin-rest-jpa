package jk.codespace.restapi.controller

import jk.codespace.restapi.entities.Student
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
        return ResponseEntity(createdStudent, HttpStatus.CREATED)
    }

    @GetMapping("v1/students")
    fun getAllStudents(): ResponseEntity<List<Student>> {
        val students = studentService.getAllStudents()
        return ResponseEntity(students, HttpStatus.OK)
    }

    @PatchMapping("v1/students")
    fun updateStudent(@RequestBody student: Student): ResponseEntity<Student> {
        val upatedStudent = studentService.updateStudent(student = student)
        return ResponseEntity(upatedStudent, HttpStatus.OK)
    }

    @DeleteMapping("v1/students/{studentid}")
    fun deleteStudentById(@PathVariable("studentid") studentId: String): ResponseEntity<Boolean> {
        val deleted = studentService.deleteStudent(studentId = studentId)
        return ResponseEntity(deleted, HttpStatus.OK)
    }

    @PostMapping("v1/students/{studentid}/courses/{coursecode}")
    fun enrollStudentInCourse(@PathVariable("studentid") studentId: String, @PathVariable("coursecode") courseCode: String): ResponseEntity<Student> {
        val student = studentService.enrollStudent(studentId = studentId, courseCode = courseCode)
        return ResponseEntity(student, HttpStatus.OK)
    }

    @DeleteMapping("v1/students/{studentid}/courses/{coursecode}")
    fun unenrollStudentInCourse(@PathVariable("studentid") studentId: String, @PathVariable("coursecode") courseCode: String): ResponseEntity<Student> {
        val student = studentService.unenrollStudent(studentId = studentId, courseCode = courseCode)
        return ResponseEntity(student, HttpStatus.OK)
    }
}