package jk.codespace.restapi.controller

import jk.codespace.restapi.dto.StudentDTO
import jk.codespace.restapi.service.StudentService
import jk.codespace.restapi.utils.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class StudentController(
    private val studentService: StudentService
) {

    @GetMapping(STUDENTS_PARAM_API_BASE_PATH)
    fun getStudentById(@PathVariable("studentid") studentId: String): ResponseEntity<StudentDTO> {
        val student = studentService.getStudent(studentId = studentId)
        return ResponseEntity(student, HttpStatus.OK)
    }

    @PostMapping(STUDENTS_API_BASE_PATH)
    fun createStudent(@RequestBody studentDTO: StudentDTO): ResponseEntity<StudentDTO> {
        val createdStudent = studentService.createStudent(studentDTO = studentDTO)
        return ResponseEntity(createdStudent, HttpStatus.CREATED)
    }

    @GetMapping(STUDENTS_API_BASE_PATH)
    fun getAllStudents(): ResponseEntity<List<StudentDTO>> {
        val students = studentService.getAllStudents()
        return ResponseEntity(students, HttpStatus.OK)
    }

    @PatchMapping(STUDENTS_API_BASE_PATH)
    fun updateStudent(@RequestBody studentDTO: StudentDTO): ResponseEntity<StudentDTO> {
        val upatedStudent = studentService.updateStudent(studentDTO = studentDTO)
        return ResponseEntity(upatedStudent, HttpStatus.OK)
    }

    @DeleteMapping(STUDENTS_PARAM_API_BASE_PATH)
    fun deleteStudentById(@PathVariable("studentid") studentId: String): ResponseEntity<Boolean> {
        val deleted = studentService.deleteStudent(studentId = studentId)
        return ResponseEntity(deleted, HttpStatus.OK)
    }

    @PostMapping(STUDENTS_ASSIGNATION_API_BASE_PATH)
    fun enrollStudentInCourse(@PathVariable("studentid") studentId: String, @PathVariable("coursecode") courseCode: String): ResponseEntity<StudentDTO> {
        val student = studentService.enrollStudent(studentId = studentId, courseCode = courseCode)
        return ResponseEntity(student, HttpStatus.OK)
    }

    @DeleteMapping(STUDENTS_ASSIGNATION_API_BASE_PATH)
    fun unenrollStudentInCourse(@PathVariable("studentid") studentId: String, @PathVariable("coursecode") courseCode: String): ResponseEntity<StudentDTO> {
        val student = studentService.unenrollStudent(studentId = studentId, courseCode = courseCode)
        return ResponseEntity(student, HttpStatus.OK)
    }
}