package jk.codespace.restapi.controller

import jk.codespace.restapi.entities.Course
import jk.codespace.restapi.entities.Lecturer
import jk.codespace.restapi.entities.Student
import jk.codespace.restapi.service.LecturerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class LecturerController(
    private val lecturerService: LecturerService
) {
    @GetMapping("v1/lecturers/{lecturerid}")
    fun getLecturerById(@PathVariable("lecturerid") lecturerId: String): ResponseEntity<Lecturer> {
        val lecturer = lecturerService.getLecturer(lecturerId = lecturerId)
        return ResponseEntity(lecturer, HttpStatus.OK)
    }

    @PostMapping("v1/lecturers")
    fun createLecturer(@RequestBody lecturer: Lecturer): ResponseEntity<Lecturer> {
        val createdLecturer = lecturerService.createLecturer(lecturer = lecturer)
        return ResponseEntity(createdLecturer, HttpStatus.CREATED)
    }

    @GetMapping("v1/lecturers")
    fun getAllLecturers(): ResponseEntity<List<Lecturer>> {
        val lecturers = lecturerService.getAllLecturers()
        return ResponseEntity(lecturers, HttpStatus.OK)
    }

    @PatchMapping("v1/lecturers")
    fun updateLecturer(@RequestBody lecturer: Lecturer): ResponseEntity<Lecturer> {
        val upatedLecturer = lecturerService.updateLecturer(lecturer = lecturer)
        return ResponseEntity(upatedLecturer, HttpStatus.OK)
    }

    @DeleteMapping("v1/lecturers/{lecturerid}")
    fun deleteLecturerById(@PathVariable("lecturerid") lecturerId: String): ResponseEntity<Boolean> {
        val deleted = lecturerService.deleteLecturer(lecturerId = lecturerId)
        return ResponseEntity(deleted, HttpStatus.OK)
    }

    @PostMapping("v1/lecturers/{lecturerid}/courses/{coursecode}")
    fun assignLecturerToCourse(@PathVariable("lecturerid") lecturerId: String, @PathVariable("coursecode") courseCode: String): ResponseEntity<Lecturer> {
        val lecturer = lecturerService.assignLecturer(lecturerId = lecturerId, courseCode = courseCode)
        return ResponseEntity(lecturer, HttpStatus.OK)
    }

    @DeleteMapping("v1/lecturers/{lecturerid}/courses/{coursecode}")
    fun deassignLecturerFromCourse(@PathVariable("lecturerid") lecturerId: String, @PathVariable("coursecode") courseCode: String): ResponseEntity<Lecturer> {
        val lecturer = lecturerService.deassignLecturer(lecturerId = lecturerId, courseCode = courseCode)
        return ResponseEntity(lecturer, HttpStatus.OK)
    }
}