package jk.codespace.restapi.controller

import jk.codespace.restapi.dto.CourseDTO
import jk.codespace.restapi.entities.Course
import jk.codespace.restapi.service.CourseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class CourseController(
    private val courseService: CourseService
) {
    @GetMapping("v1/courses/{coursecode}")
    fun getCourseByCode(@PathVariable("coursecode") courseCode: String): ResponseEntity<CourseDTO> {
        val course = courseService.getCourse(courseCode = courseCode)
        return ResponseEntity(course, HttpStatus.OK)
    }

    @GetMapping("v2/courses/{coursecode}")
    fun getCourseByCodeV2(@PathVariable("coursecode") courseCode: String): ResponseEntity<Course> {
        val course = courseService.getCourseNew(courseCode = courseCode)
        return ResponseEntity(course, HttpStatus.OK)
    }


    @PostMapping("v1/courses")
    fun createcourse(@RequestBody course: CourseDTO): ResponseEntity<CourseDTO> {
        val createdCourse = courseService.createCourse(courseDTO = course)
        return ResponseEntity(createdCourse, HttpStatus.CREATED)
    }

    @GetMapping("v1/courses")
    fun getAllcourses(): ResponseEntity<List<CourseDTO>> {
        val courses = courseService.getAllCourses()
        return ResponseEntity(courses, HttpStatus.OK)
    }

    @PatchMapping("v1/courses")
    fun updatecourse(@RequestBody course: CourseDTO): ResponseEntity<CourseDTO> {
        val upatedCourse = courseService.updateCourse(courseDTO = course)
        return ResponseEntity(upatedCourse, HttpStatus.OK)
    }

    @DeleteMapping("v1/courses/{coursecode}")
    fun deletecourseById(@PathVariable("coursecode") courseCode: String): ResponseEntity<Boolean> {
        val deleted = courseService.deleteCourse(courseCode = courseCode)
        return ResponseEntity(deleted, HttpStatus.OK)
    }
}