package jk.codespace.restapi.controller

import jk.codespace.restapi.dto.LecturerDTO
import jk.codespace.restapi.service.LecturerService
import jk.codespace.restapi.utils.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class LecturerController(
    private val lecturerService: LecturerService
) {
    @GetMapping(LECTURERS_PARAM_API_BASE_PATH)
    fun getLecturerById(@PathVariable("lecturerid") lecturerId: String): ResponseEntity<LecturerDTO> {
        val lecturer = lecturerService.getLecturer(lecturerId = lecturerId)
        return ResponseEntity(lecturer, HttpStatus.OK)
    }

    @PostMapping(LECTURERS_API_BASE_PATH)
    fun createLecturer(@RequestBody lecturer: LecturerDTO): ResponseEntity<LecturerDTO> {
        val createdLecturer = lecturerService.createLecturer(lecturer = lecturer)
        return ResponseEntity(createdLecturer, HttpStatus.CREATED)
    }

    @GetMapping(LECTURERS_API_BASE_PATH)
    fun getAllLecturers(): ResponseEntity<List<LecturerDTO>> {
        val lecturers = lecturerService.getAllLecturers()
        return ResponseEntity(lecturers, HttpStatus.OK)
    }

    @PatchMapping(LECTURERS_API_BASE_PATH)
    fun updateLecturer(@RequestBody lecturer: LecturerDTO): ResponseEntity<LecturerDTO> {
        val upatedLecturer = lecturerService.updateLecturer(lecturer = lecturer)
        return ResponseEntity(upatedLecturer, HttpStatus.OK)
    }

    @DeleteMapping(LECTURERS_PARAM_API_BASE_PATH)
    fun deleteLecturerById(@PathVariable("lecturerid") lecturerId: String): ResponseEntity<Boolean> {
        val deleted = lecturerService.deleteLecturer(lecturerId = lecturerId)
        return ResponseEntity(deleted, HttpStatus.OK)
    }

    @PostMapping(LECTURERS_ASSIGNATION_API_BASE_PATH)
    fun assignLecturerToCourse(@PathVariable("lecturerid") lecturerId: String, @PathVariable("coursecode") courseCode: String): ResponseEntity<LecturerDTO> {
        val lecturer = lecturerService.assignLecturer(lecturerId = lecturerId, courseCode = courseCode)
        return ResponseEntity(lecturer, HttpStatus.OK)
    }

    @DeleteMapping(LECTURERS_ASSIGNATION_API_BASE_PATH)
    fun deassignLecturerFromCourse(@PathVariable("lecturerid") lecturerId: String, @PathVariable("coursecode") courseCode: String): ResponseEntity<LecturerDTO> {
        val lecturer = lecturerService.deassignLecturer(lecturerId = lecturerId, courseCode = courseCode)
        return ResponseEntity(lecturer, HttpStatus.OK)
    }
}