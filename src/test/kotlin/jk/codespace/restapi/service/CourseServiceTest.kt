package jk.codespace.restapi.service

import io.mockk.every
import io.mockk.mockk
import jk.codespace.restapi.entities.Course
import jk.codespace.restapi.exception.AppException
import jk.codespace.restapi.repository.CourseRepository
import jk.codespace.restapi.repository.LecturerRepository
import jk.codespace.restapi.repository.StudentRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class CourseServiceTest {

    // Mock out the Student Repository
    private val studentRepository = mockk<StudentRepository>()

    // Mock out the Course Repository
    private val courseRepository = mockk<CourseRepository>()

    // Mock out the Course Repository
    private val lecturerRepository = mockk<LecturerRepository>()

    // Create instance of class under test
    private val courseService = CourseService(studentRepository = studentRepository, courseRepository = courseRepository, lecturerRepository = lecturerRepository)

    @Test
    fun getCourseSuccess() {
        val courseCode = "111"
        val course = Course(id = 12345678, courseCode = courseCode, courseName = "IT", courseDescription = "Degree in IT")
        every { courseRepository.findByCourseCode(courseCode)} returns course

        // Invoke the method under test
        val response = courseService.getCourse(courseCode)

        // Test the response
        Assertions.assertThat(response?.courseCode).isEqualTo("111")
        Assertions.assertThat(response?.courseName).isEqualTo("IT")
        Assertions.assertThat(response?.courseDescription).isEqualTo("Degree in IT")
    }

    @Test
    fun getCourseNotFound() {
        val courseCode = "12345"
        every {courseRepository.findByCourseCode(courseCode)} throws AppException(statusCode = 404, reason = "Cannot find Course with code $courseCode")

        val exception = org.junit.jupiter.api.Assertions.assertThrows(AppException::class.java) {
            courseService.getCourse(courseCode)
        }
        Assertions.assertThat(exception.errorMessage).isEqualTo("Cannot find Course with code $courseCode")
        Assertions.assertThat(exception.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun getAllCoursesSuccess() {
        every { courseRepository.findAll()} returns arrayListOf(Course(12345, courseCode = "123", courseName = "IT", courseDescription = "Degree in IT"),Course(67891,
            courseCode = "456", courseName = "Science", courseDescription = "Degree in Science"))

        val response = courseService.getAllCourses()

        // Test the response
        Assertions.assertThat(response[0].courseCode).isEqualTo("123")
        Assertions.assertThat(response[0].courseName).isEqualTo("IT")
        Assertions.assertThat(response[0].courseDescription).isEqualTo("Degree in IT")
        Assertions.assertThat(response[1].courseCode).isEqualTo("456")
        Assertions.assertThat(response[1].courseName).isEqualTo("Science")
        Assertions.assertThat(response[1].courseDescription).isEqualTo("Degree in Science")
    }

    @Test
    fun getAllCoursesNoCoursesInDB() {
        every { courseRepository.findAll()} returns emptyList()
        // Invoke the method under test
        val response = courseService.getAllCourses()

        // Test the response
        Assertions.assertThat(response.size).isEqualTo(0)
    }

    @Test
    fun createCourseSuccess() {
        val persistedCourse = Course(id = 123456, courseCode = "1234", courseName = "IT", courseDescription = "Degree in IT")
        val inputCourse = Course(courseCode = "1234", courseName = "IT", courseDescription = "Degree in IT")
        every {courseRepository.save(any())} returns persistedCourse
        every {courseRepository.findByCourseCode(inputCourse.courseCode)} returns null

        val response = courseService.createCourse(inputCourse)
        Assertions.assertThat(response.courseCode).isEqualTo("1234")
        Assertions.assertThat(response.courseName).isEqualTo("IT")
        Assertions.assertThat(response.courseDescription).isEqualTo("Degree in IT")
    }

    @Test
    fun createCourseAlreadyFound() {
        val existingCourse = Course(id = 123456, courseCode = "1234", courseName = "IT", courseDescription = "Degree in IT")
        val inputCourse = Course(courseCode = "1234", courseName = "IT", courseDescription = "Degree in IT")
        every {courseRepository.findByCourseCode(inputCourse.courseCode)} returns existingCourse

        val exception = org.junit.jupiter.api.Assertions.assertThrows(AppException::class.java) {
            courseService.createCourse(inputCourse)
        }
        Assertions.assertThat(exception.errorMessage).isEqualTo("A Course with Course code: 1234 already exists")
        Assertions.assertThat(exception.httpStatus).isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun updateCourseSuccess() {
        val course = Course(courseCode = "1234", courseName = "Information Technology", courseDescription = "Degree in IT")
        every {courseRepository.save(any())} returns Course(id = 123456, courseCode = "1234", courseName = "Information Technology", courseDescription = "Degree in IT")
        every {courseRepository.findByCourseCode(course.courseCode)} returns Course(id = 123456, courseCode = "1234", courseName = "IT", courseDescription = "Degree in IT")

        val response = courseService.updateCourse(course)
        Assertions.assertThat(response.courseCode).isEqualTo("1234")
        Assertions.assertThat(response.courseName).isEqualTo("Information Technology")
        Assertions.assertThat(response.courseDescription).isEqualTo("Degree in IT")
    }

    @Test
    fun updateCourseNotFound() {
        val course = Course(courseCode = "1234", courseName = "IT", courseDescription = "Degree in IT")

        every {courseRepository.findByCourseCode(course.courseCode)} throws AppException(statusCode = 404, reason = "A Course with Course code: 1234 does not exist. Cannot update")

        val exception = org.junit.jupiter.api.Assertions.assertThrows(AppException::class.java) {
            courseService.updateCourse(course)
        }
        Assertions.assertThat(exception.errorMessage).isEqualTo("A Course with Course code: 1234 does not exist. Cannot update")
        Assertions.assertThat(exception.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun deleteCourseSuccess() {
        val course = Course(courseCode = "1234", courseName = "IT", courseDescription = "Degree in IT")
        every {courseRepository.findByCourseCode(course.courseCode)} returns Course(id = 123456, courseCode = "1234", courseName = "IT", courseDescription = "Degree in IT")
        every {courseRepository.delete(any())} returns Unit
        val response = courseService.deleteCourse(course.courseCode)
        Assertions.assertThat(response).isTrue
    }

    @Test
    fun deleteCourseNotFound() {
        val course = Course(courseCode = "1234", courseName = "IT", courseDescription = "Degree in IT")
        every {courseRepository.findByCourseCode(course.courseCode)} throws AppException(statusCode = 404, reason = "A Course with Course code: 1234 does not exist. Cannot delete")
        val exception = org.junit.jupiter.api.Assertions.assertThrows(AppException::class.java) {
            courseService.deleteCourse(course.courseCode)
        }
        Assertions.assertThat(exception.errorMessage).isEqualTo("A Course with Course code: 1234 does not exist. Cannot delete")
        Assertions.assertThat(exception.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun deleteCourseUnexpectedError() {
        val course = Course(courseCode = "1234", courseName = "Jimmy", courseDescription = "Hughes")
        every {courseRepository.findByCourseCode(course.courseCode)} returns Course(id = 123456, courseCode = "1234", courseName = "IT", courseDescription = "Degree in IT")
        every {courseRepository.delete(any())} throws Exception("DB Does not exist")

        val exception = org.junit.jupiter.api.Assertions.assertThrows(AppException::class.java) {
            courseService.deleteCourse(course.courseCode)
        }
        Assertions.assertThat(exception.errorMessage).isEqualTo("Unexpected error encountered deleting Course with Course Code 1234")
        Assertions.assertThat(exception.httpStatus).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }



}