package jk.codespace.restapi.controller

import jk.codespace.restapi.dto.StudentDTO
import jk.codespace.restapi.service.StudentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class StudentController(
    private val studentService: StudentService
) {

    @GetMapping("v1/students/{studentid}")
    fun getStudentById(@PathVariable("studentid") studentId: String): ResponseEntity<StudentDTO> {
        val student = studentService.getStudent(studentId = studentId)
        return ResponseEntity(student, HttpStatus.OK)
    }

    @PostMapping("v1/students")
    fun createStudent(@RequestBody student: StudentDTO): ResponseEntity<StudentDTO> {
        val createdStudent = studentService.createStudent(studentDTO = student)
        return ResponseEntity(createdStudent, HttpStatus.CREATED)
    }

    @GetMapping("v1/students")
    fun getAllStudents(): ResponseEntity<List<StudentDTO>> {
        val students = studentService.getAllStudents()
        return ResponseEntity(students, HttpStatus.OK)
    }

    @PatchMapping("v1/students")
    fun updateStudent(@RequestBody student: StudentDTO): ResponseEntity<StudentDTO> {
        val upatedStudent = studentService.updateStudent(studentDTO = student)
        return ResponseEntity(upatedStudent, HttpStatus.OK)
    }

    @DeleteMapping("v1/students/{studentid}")
    fun deleteStudentById(@PathVariable("studentid") studentId: String): ResponseEntity<Boolean> {
        val deleted = studentService.deleteStudent(studentId = studentId)
        return ResponseEntity(deleted, HttpStatus.OK)
    }

    @PostMapping("v1/students/{studentid}/courses/{coursecode}")
    fun enrollStudentInCourse(@PathVariable("studentid") studentId: String, @PathVariable("coursecode") courseCode: String): ResponseEntity<StudentDTO> {
        val student = studentService.enrollStudent(studentId = studentId, courseCode = courseCode)
        return ResponseEntity(student, HttpStatus.OK)
    }


}