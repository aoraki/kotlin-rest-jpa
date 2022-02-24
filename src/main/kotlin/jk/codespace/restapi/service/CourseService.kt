package jk.codespace.restapi.service

import jk.codespace.restapi.entities.Course
import jk.codespace.restapi.entities.Student
import jk.codespace.restapi.exception.AppException
import jk.codespace.restapi.repository.CourseRepository
import jk.codespace.restapi.repository.StudentRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val studentRepository: StudentRepository
) {
    private val log = KotlinLogging.logger {}

    fun getCourse(courseCode: String?): Course? {
        val course: Course? = courseRepository.findByCourseCode(courseCode!!)
        if(course != null) return course else throw AppException(statusCode = 404, reason = "Cannot find Course with code $courseCode")
    }

    fun getAllCourses(): List<Course> {
        log.info { "Attempting to get all Courses" }
        val courses = courseRepository.findAll() as List<Course>
        return courses
    }

    fun createCourse(course: Course): Course {
        log.info { "Attempting to create Course with Code : ${course.courseCode}" }

        val checkCourse: Course? = courseRepository.findByCourseCode(course.courseCode)
        if(checkCourse != null){
            throw AppException(statusCode = 409, reason = "A Course with Course code: ${course.courseCode} already exists")
        }

        val retCourse: Course = courseRepository.save(course)
        return retCourse
    }

    fun updateCourse(course: Course): Course {
        log.info { "Attempting to update Course with code : ${course.courseCode}" }
        val existingCourse: Course = courseRepository.findByCourseCode(course.courseCode) ?: throw AppException(statusCode = 404, reason = "A Course with Course code: ${course.courseCode} does not exist.  Cannot update")
        course.id = existingCourse.id
        val retCourse: Course = courseRepository.save(course)
        return retCourse
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

        // The course can now be safely deleted
        try {
            courseRepository.delete(course)
        } catch (ex: Exception) {
            throw AppException(500, "Unexpected error encountered deleting Course with Course Code $courseCode")
        }
        return true
    }
}