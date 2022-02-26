package jk.codespace.restapi.service

import jk.codespace.restapi.entities.Lecturer
import jk.codespace.restapi.exception.AppException
import jk.codespace.restapi.repository.CourseRepository
import jk.codespace.restapi.repository.LecturerRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class LecturerService (
    private val courseRepository: CourseRepository,
    private val lecturerRepository: LecturerRepository
) {
    private val log = KotlinLogging.logger {}

    fun getLecturer(lecturerId: String?): Lecturer? {
        val lecturer: Lecturer? = lecturerRepository.findByLecturerId(lecturerId!!)
        if(lecturer != null) return lecturer else throw AppException(statusCode = 404, reason = "Cannot find lecturer with id $lecturerId")
    }

    fun getAllLecturers(): List<Lecturer> {
        log.info { "Attempting to get all lecturers" }
        return lecturerRepository.findAll() as List<Lecturer>
    }

    fun createLecturer(lecturer: Lecturer): Lecturer {
        log.info { "Attempting to create Lecturer with Id : ${lecturer.lecturerId}" }

        val checkLecturer: Lecturer? = lecturerRepository.findByLecturerId(lecturer.lecturerId)
        if (checkLecturer != null) {
            throw AppException(
                statusCode = 409,
                reason = "A lecturer with lecturer id: ${lecturer.lecturerId} already exists"
            )
        }
        return lecturerRepository.save(lecturer)
    }

    fun updateLecturer(lecturer: Lecturer): Lecturer {
        log.info { "Attempting to update Lecturer with Id : ${lecturer.lecturerId}" }
        val existingLecturer: Lecturer = lecturerRepository.findByLecturerId(lecturer.lecturerId) ?: throw AppException(
            statusCode = 404,
            reason = "A lecturer with lecturer id: ${lecturer.lecturerId} does not exist.  Cannot update"
        )
        lecturer.id = existingLecturer.id
        return lecturerRepository.save(lecturer)
    }

    fun deleteLecturer(lecturerId: String): Boolean {
        log.info { "Attempting to delete Lecturer with Id : ${lecturerId}" }

        val lecturer: Lecturer = lecturerRepository.findByLecturerId(lecturerId)  ?: throw AppException(statusCode = 404, reason = "A lecturer with lecturer id: $lecturerId does not exist.  Cannot delete")

        /*
        val courses: Set<Course> = student.courses

        // Ensure courses that the student is currently enrolled in are disassociated from the student
        for(course in courses){
            course.removeStudent(student)
            courseRepository.save(course)
        }*/

        try {
            lecturerRepository.delete(lecturer)
        } catch (ex: Exception) {
            throw AppException(500, "Unexpected error encountered deleting lecturer with lecturer id $lecturerId")
        }
        return true
    }
}