package jk.codespace.restapi.service

import jk.codespace.restapi.dto.CourseDTO
import jk.codespace.restapi.dto.CourseDTOShallow
import jk.codespace.restapi.entities.Course
import jk.codespace.restapi.entities.Student
import jk.codespace.restapi.exception.AppException
import jk.codespace.restapi.repository.CourseRepository
import jk.codespace.restapi.repository.LecturerRepository
import jk.codespace.restapi.repository.StudentRepository
import jk.codespace.restapi.utils.Konversion
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val studentRepository: StudentRepository,
    private val lecturerRepository: LecturerRepository
) {
    private val log = KotlinLogging.logger {}
    private val conversion = Konversion()

    fun getCourse(courseCode: String?): CourseDTO? {
        val course: Course? = courseRepository.findByCourseCode(courseCode!!)
        if(course != null) return conversion.convertCourseToDTO(course) else throw AppException(statusCode = 404, reason = "Cannot find Course with code $courseCode")
    }

    fun getAllCourses(): List<CourseDTO> {
        log.info { "Attempting to get all Courses" }
        val courses = courseRepository.findAll() as List<Course>

        val courseDTOs = ArrayList<CourseDTO>()
        for(course in courses){
            courseDTOs.add(conversion.convertCourseToDTO(course))
        }
        return courseDTOs

    }

    fun createCourse(courseDTO: CourseDTOShallow): CourseDTO {
        log.info { "Attempting to create Course with Code : ${courseDTO.courseCode}" }
        val checkCourse: Course? = courseRepository.findByCourseCode(courseDTO.courseCode)
        if (checkCourse != null) {
            throw AppException(statusCode = 409, reason = "A Course with Course code: ${courseDTO.courseCode} already exists")
        }
        // ensure that the students field is empty
        //courseDTO.students = mutableSetOf(StudentDTOShallow())
        val course: Course = conversion.convertCourseDTOShallowToCourse(courseDTO)
        val retCourse: Course = courseRepository.save(course)
        return conversion.convertCourseToDTO(retCourse)
    }

    fun updateCourse(courseDTO: CourseDTOShallow): CourseDTO {
        log.info { "Attempting to update Course with code : ${courseDTO.courseCode}" }
        val existingCourse: Course = courseRepository.findByCourseCode(courseDTO.courseCode) ?: throw AppException(statusCode = 404, reason = "A Course with Course code: ${courseDTO.courseCode} does not exist.  Cannot update")

        // Update the existing course with the values from the shallow object.  We don't want to update the lecturer in this operation
        // That is done by a different operation
        existingCourse.courseName = courseDTO.courseName
        existingCourse.courseCode = courseDTO.courseCode
        existingCourse.courseDescription = courseDTO.courseDescription

        val retCourse: Course = courseRepository.save(existingCourse)
        return conversion.convertCourseToDTO(retCourse)
    }

    fun deleteCourse(courseCode: String): Boolean {
        log.info { "Attempting to delete Course with Id : ${courseCode}" }

        val course: Course = courseRepository.findByCourseCode(courseCode)  ?: throw AppException(statusCode = 404, reason = "A Course with Course code: $courseCode does not exist.  Cannot delete")
        val students: Set<Student> = course.students

        // Ensure students that are currently enrolled in the course are disassociated from the course
        for(student in students){
            student.removeCourse(course)
            studentRepository.save(student)
        }

        // Ensure lecturers that are assigned to the course are deassigned
        val lecturer = course.lecturer
        if (lecturer != null) {
            // Set the lecturers course to null and then save the lecturer object
            lecturer.course = null
            lecturerRepository.save(lecturer)
        }

        // The course can now be safely deleted
        try {
            courseRepository.delete(course)
        } catch (ex: Exception) {
            throw AppException(500, "Unexpected error encountered deleting Course with Course Code $courseCode")
        }
        return true
    }
}