package jk.codespace.restapi.service

import jk.codespace.restapi.entities.Course
import jk.codespace.restapi.entities.Lecturer
import jk.codespace.restapi.exception.AppException
import jk.codespace.restapi.repository.CourseRepository
import jk.codespace.restapi.repository.LecturerRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Suppress("UNCHECKED_CAST")
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
            throw AppException(statusCode = 409, reason = "A lecturer with lecturer id: ${lecturer.lecturerId} already exists")
        }
        return lecturerRepository.save(lecturer)
    }

    fun updateLecturer(lecturer: Lecturer): Lecturer {
        log.info { "Attempting to update Lecturer with Id : ${lecturer.lecturerId}" }
        val existingLecturer: Lecturer = lecturerRepository.findByLecturerId(lecturer.lecturerId) ?: throw AppException(statusCode = 404, reason = "A lecturer with lecturer id: ${lecturer.lecturerId} does not exist.  Cannot update")
        lecturer.id = existingLecturer.id
        return lecturerRepository.save(lecturer)
    }

    fun deleteLecturer(lecturerId: String): Boolean {
        log.info { "Attempting to delete Lecturer with Id : ${lecturerId}" }

        val lecturer: Lecturer = lecturerRepository.findByLecturerId(lecturerId)  ?: throw AppException(statusCode = 404, reason = "A lecturer with lecturer id: $lecturerId does not exist.  Cannot delete")

        try {
            lecturerRepository.delete(lecturer)
        } catch (ex: Exception) {
            throw AppException(500, "Unexpected error encountered deleting lecturer with lecturer id $lecturerId")
        }
        return true
    }

    fun assignLecturer(lecturerId: String, courseCode: String): Lecturer {
        log.info { "Attempting to assign Lecturer with Id : ${lecturerId} to course with code : ${courseCode}" }
        val lecturer: Lecturer = lecturerRepository.findByLecturerId(lecturerId) ?: throw AppException(
            statusCode = 404,
            reason = "A lecturer with lecturer id: $lecturerId does not exist.  Cannot complete assignment"
        )
        val course: Course = courseRepository.findByCourseCode(courseCode) ?: throw AppException(
            statusCode = 404,
            reason = "A course with  code: $courseCode does not exist.  Cannot complete assignment"
        )

        if (course.lecturer != null) {
            throw AppException(
                statusCode = 409,
                reason = "Course ${courseCode} already has a lecturer assigned to it"
            )
        }
        lecturer.course = course
        return lecturerRepository.save(lecturer)
    }

    fun deassignLecturer(lecturerId: String, courseCode: String): Lecturer {
        log.info { "Attempting to assign Lecturer with Id : ${lecturerId} to course with code : ${courseCode}" }
        val lecturer: Lecturer = lecturerRepository.findByLecturerId(lecturerId) ?: throw AppException(
            statusCode = 404,
            reason = "A lecturer with lecturer id: $lecturerId does not exist.  Cannot complete assignment"
        )
        lecturer.course = null
        return lecturerRepository.save(lecturer)
    }
}