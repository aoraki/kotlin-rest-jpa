package jk.codespace.restapi.service

import jk.codespace.restapi.dto.LecturerDTO
import jk.codespace.restapi.entities.Course
import jk.codespace.restapi.entities.Lecturer
import jk.codespace.restapi.exception.AppException
import jk.codespace.restapi.repository.CourseRepository
import jk.codespace.restapi.repository.LecturerRepository
import jk.codespace.restapi.utils.Konversion
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Suppress("UNCHECKED_CAST")
@Service
class LecturerService (
    private val courseRepository: CourseRepository,
    private val lecturerRepository: LecturerRepository
) {
    private val log = KotlinLogging.logger {}
    private val conversion = Konversion()

    fun getLecturer(lecturerId: String?): LecturerDTO? {
        val lecturer: Lecturer? = lecturerRepository.findByLecturerId(lecturerId!!)
        if(lecturer != null) return conversion.convertLecturerToDTO(lecturer) else throw AppException(statusCode = 404, reason = "Cannot find lecturer with id $lecturerId")
    }

    fun getAllLecturers(): List<LecturerDTO> {
        log.info { "Attempting to get all lecturers" }
        val lecturers =  lecturerRepository.findAll() as List<Lecturer>
        val lecturerDTOs = ArrayList<LecturerDTO>()
        for(lecturer in lecturers){
            lecturerDTOs.add(conversion.convertLecturerToDTO(lecturer))
        }
        return lecturerDTOs
    }

    fun createLecturer(lecturer: LecturerDTO): LecturerDTO {
        log.info { "Attempting to create Lecturer with Id : ${lecturer.lecturerId}" }

        val checkLecturer: Lecturer? = lecturerRepository.findByLecturerId(lecturer.lecturerId)
        if (checkLecturer != null) {
            throw AppException(statusCode = 409, reason = "A lecturer with lecturer id: ${lecturer.lecturerId} already exists")
        }

        val lecturer: Lecturer = conversion.convertLecturerDTOToLecturer(lecturer)
        val retLecturer: Lecturer = lecturerRepository.save(lecturer)
        return conversion.convertLecturerToDTO(retLecturer)
    }


    fun updateLecturer(lecturer: LecturerDTO): LecturerDTO {
        log.info { "Attempting to update Lecturer with Id : ${lecturer.lecturerId}" }
        val existingLecturer: Lecturer = lecturerRepository.findByLecturerId(lecturer.lecturerId) ?: throw AppException(statusCode = 404, reason = "A lecturer with lecturer id: ${lecturer.lecturerId} does not exist.  Cannot update")

        val updatedLecturer: Lecturer = conversion.convertLecturerDTOToLecturerWithDatabaseId(lecturer, existingLecturer.id)
        val retLecturer: Lecturer = lecturerRepository.save(updatedLecturer)
        return conversion.convertLecturerToDTO(retLecturer)
    }

    fun deleteLecturer(lecturerId: String): Boolean {
        log.info { "Attempting to delete Lecturer with Id : ${lecturerId}" }

        val lecturer: Lecturer = lecturerRepository.findByLecturerId(lecturerId)  ?: throw AppException(statusCode = 404, reason = "A lecturer with lecturer id: $lecturerId does not exist.  Cannot delete")

        val course = lecturer.course
        if (course != null) {
            // Set the courses lecturer to null and then save the course object
            course.lecturer = null
            courseRepository.save(course)
        }

        try {
            lecturerRepository.delete(lecturer)
        } catch (ex: Exception) {
            throw AppException(500, "Unexpected error encountered deleting lecturer with lecturer id $lecturerId")
        }
        return true
    }

    fun assignLecturer(lecturerId: String, courseCode: String): LecturerDTO {
        log.info { "Attempting to assign Lecturer with Id : ${lecturerId} to course with code : ${courseCode}" }
        val lecturer: Lecturer = lecturerRepository.findByLecturerId(lecturerId) ?: throw AppException(statusCode = 404, reason = "A lecturer with lecturer id: $lecturerId does not exist.  Cannot complete assignment")
        val course: Course = courseRepository.findByCourseCode(courseCode) ?: throw AppException(statusCode = 404, reason = "A course with  code: $courseCode does not exist.  Cannot complete assignment")

        if (course.lecturer != null) {
            throw AppException(statusCode = 409, reason = "Course ${courseCode} already has a lecturer assigned to it")
        }
        lecturer.course = course
        return conversion.convertLecturerToDTO(lecturerRepository.save(lecturer))
    }

    fun deassignLecturer(lecturerId: String, courseCode: String): LecturerDTO {
        log.info { "Attempting to assign Lecturer with Id : ${lecturerId} to course with code : ${courseCode}" }
        val lecturer: Lecturer = lecturerRepository.findByLecturerId(lecturerId) ?: throw AppException(statusCode = 404, reason = "A lecturer with lecturer id: $lecturerId does not exist.  Cannot complete deassignment")
        val course: Course = courseRepository.findByCourseCode(courseCode) ?: throw AppException(statusCode = 404, reason = "A course with  code: $courseCode does not exist.  Cannot complete deassignment")
        lecturer.course = null
        return conversion.convertLecturerToDTO(lecturerRepository.save(lecturer))
    }
}