package jk.codespace.restapi.service

import io.mockk.every
import io.mockk.mockk
import jk.codespace.restapi.dto.StudentDTO
import jk.codespace.restapi.entities.Course
import jk.codespace.restapi.entities.Student
import jk.codespace.restapi.exception.AppException
import jk.codespace.restapi.repository.CourseRepository
import jk.codespace.restapi.repository.StudentRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class StudentServiceTest {

    // Mock out the Student Repository
    private val studentRepository = mockk<StudentRepository>()

    // Mock out the Course Repository
    private val courseRepository = mockk<CourseRepository>()

    // Create instance of class under test
    private val studentService = StudentService(studentRepository = studentRepository, courseRepository = courseRepository)

    @Test
    fun getStudentSuccess() {
        val studentId = "12345"
        val student = Student(id = 12345678, studentId = studentId, firstName = "Jim", lastName = "Hughes")
        every {studentRepository.findByStudentId(studentId)} returns student

        // Invoke the method under test
        val response = studentService.getStudent(studentId)

        // Test the response
        assertThat(response?.studentId).isEqualTo("12345")
        assertThat(response?.firstName).isEqualTo("Jim")
        assertThat(response?.lastName).isEqualTo("Hughes")
    }

    @Test
    fun getStudentNotFound() {
        val studentId = "12345"
        every {studentRepository.findByStudentId(studentId)} throws AppException(statusCode = 404, reason = "Cannot find student with id 1234")

        val exception = Assertions.assertThrows(AppException::class.java) {
            studentService.getStudent(studentId)
        }
        assertThat(exception.errorMessage).isEqualTo("Cannot find student with id 1234")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun getAllStudentsSuccess() {
        every {studentRepository.findAll()} returns arrayListOf(Student(12345,studentId = "123", firstName = "Jay", lastName = "Parker"),Student(67891,studentId = "456", firstName = "Pamela", lastName = "Jones"))

        val response = studentService.getAllStudents()

        // Test the response
        assertThat(response[0].studentId).isEqualTo("123")
        assertThat(response[0].firstName).isEqualTo("Jay")
        assertThat(response[0].lastName).isEqualTo("Parker")
        assertThat(response[1].studentId).isEqualTo("456")
        assertThat(response[1].firstName).isEqualTo("Pamela")
        assertThat(response[1].lastName).isEqualTo("Jones")
    }


    @Test
    fun getAllStudentsNoStudentsInDB() {
        every {studentRepository.findAll()} returns emptyList()
        // Invoke the method under test
        val response = studentService.getAllStudents()

        // Test the response
        assertThat(response.size).isEqualTo(0)
    }

    @Test
    fun createStudentSuccess() {
        val persistedStudent = Student(id = 123456, studentId = "1234", firstName = "Jim", lastName = "Hughes")
        val inputStudentDTO = StudentDTO(studentId = "1234", firstName = "Jim", lastName = "Hughes", courses = mutableSetOf())
        every {studentRepository.save(any())} returns persistedStudent
        every {studentRepository.findByStudentId(inputStudentDTO.studentId)} returns null

        val response = studentService.createStudent(inputStudentDTO)
        assertThat(response.studentId).isEqualTo("1234")
        assertThat(response.firstName).isEqualTo("Jim")
        assertThat(response.lastName).isEqualTo("Hughes")
    }

    @Test
    fun createStudentAlreadyFound() {
        val existingStudent = Student(id = 123456, studentId = "1234", firstName = "Jim", lastName = "Hughes")
        val inputStudentDTO = StudentDTO(studentId = "1234", firstName = "Jim", lastName = "Hughes", courses = mutableSetOf())
        every {studentRepository.findByStudentId(inputStudentDTO.studentId)} returns existingStudent

        val exception = Assertions.assertThrows(AppException::class.java) {
            studentService.createStudent(inputStudentDTO)
        }
        assertThat(exception.errorMessage).isEqualTo("A student with student code: 1234 already exists")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun updateStudentSuccess() {
        val studentDTO = StudentDTO(studentId = "1234", firstName = "Jimmy", lastName = "Hughes", courses = mutableSetOf())
        every {studentRepository.save(any())} returns Student(id = 123456, studentId = "1234", firstName = "Jimmy", lastName = "Hughes")
        every {studentRepository.findByStudentId(studentDTO.studentId)} returns Student(id = 123456, studentId = "1234", firstName = "Jim", lastName = "Hughes")

        val response = studentService.updateStudent(studentDTO)
        assertThat(response.studentId).isEqualTo("1234")
        assertThat(response.firstName).isEqualTo("Jimmy")
        assertThat(response.lastName).isEqualTo("Hughes")
    }

    @Test
    fun updateStudentNotFound() {
        val studentDTO = StudentDTO(studentId = "1234", firstName = "Jimmy", lastName = "Hughes", courses = mutableSetOf())
        every {studentRepository.findByStudentId(studentDTO.studentId)} throws AppException(statusCode = 404, reason = "A student with student code: ${studentDTO.studentId} does not exist.  Cannot update")

        val exception = Assertions.assertThrows(AppException::class.java) {
            studentService.updateStudent(studentDTO)
        }
        assertThat(exception.errorMessage).isEqualTo("A student with student code: ${studentDTO.studentId} does not exist.  Cannot update")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun deleteStudentSuccess() {
        val studentId = "1234"
        val course = Course(courseCode = "BSC-111", courseName = "Joe", courseDescription = "Blggs")
        val courseSet = setOf(course)

        every {studentRepository.findByStudentId(studentId)} returns Student(id = 123456, studentId = studentId, firstName = "Jim", lastName = "Hughes", courses = courseSet)
        every {studentRepository.delete(any())} returns Unit
        every {courseRepository.save(any())} returns course

        val response = studentService.deleteStudent(studentId)
        assertThat(response).isTrue
    }

    @Test
    fun deleteStudentNotFound() {
        val student = Student(studentId = "1234", firstName = "Jimmy", lastName = "Hughes")
        every {studentRepository.findByStudentId(student.studentId)} throws AppException(statusCode = 404, reason = "A student with student code: 1234 does not exist. Cannot delete")
        val exception = Assertions.assertThrows(AppException::class.java) {
            studentService.deleteStudent(student.studentId)
        }
        assertThat(exception.errorMessage).isEqualTo("A student with student code: 1234 does not exist. Cannot delete")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun deleteStudentUnexpectedError() {
        val student = Student(studentId = "1234", firstName = "Jimmy", lastName = "Hughes")
        every {studentRepository.findByStudentId(student.studentId)} returns Student(id = 123456, studentId = "1234", firstName = "Jim", lastName = "Hughes")
        every {studentRepository.delete(any())} throws Exception("DB Does not exist")

        val exception = Assertions.assertThrows(AppException::class.java) {
            studentService.deleteStudent(student.studentId)
        }
        assertThat(exception.errorMessage).isEqualTo("Unexpected error encountered deleting student with student id 1234")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun enrollStudentInCourseSuccess() {
        val student = Student(id = 123456, studentId = "1234", firstName = "Jimmy", lastName = "Hughes")
        val course = Course(id = 55555, courseCode = "BSC-111", courseName = "IT", courseDescription = "Degree in IT")
        student.addCourse(course)
        every {studentRepository.findByStudentId(student.studentId)} returns student
        every {courseRepository.findByCourseCode(course.courseCode)} returns course
        every {studentRepository.save(any())} returns student

        val response = studentService.enrollStudent(studentId = student.studentId, courseCode = course.courseCode)
        assertThat(response.studentId).isEqualTo("1234")
        assertThat(response.firstName).isEqualTo("Jimmy")
        assertThat(response.lastName).isEqualTo("Hughes")
        assertThat(response.courses.first().courseCode).isEqualTo("BSC-111")
        assertThat(response.courses.first().courseName).isEqualTo("IT")
        assertThat(response.courses.first().courseDescription).isEqualTo("Degree in IT")
    }

    @Test
    fun enrollStudentInCourseStudentNotFound() {
        val studentId = "1234"
        val courseCode = "BSC-123"

        every {studentRepository.findByStudentId(studentId)} throws AppException(statusCode = 404, reason = "A student with student code: $studentId does not exist.  Cannot complete enrolment")

        val exception = Assertions.assertThrows(AppException::class.java) {
            studentService.enrollStudent(studentId = studentId, courseCode = courseCode)
        }
        assertThat(exception.errorMessage).isEqualTo("A student with student code: $studentId does not exist.  Cannot complete enrolment")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun enrollStudentInCourseCourseNotFound() {
        val studentId = "1234"
        val courseCode = "BSC-123"
        val student = Student(id = 123456, studentId = "1234", firstName = "Jimmy", lastName = "Hughes")

        every {studentRepository.findByStudentId(studentId)} returns student
        every {courseRepository.findByCourseCode(courseCode)} throws AppException(statusCode = 404, reason = "A course with  code: $courseCode does not exist.  Cannot complete enrolment")

        val exception = Assertions.assertThrows(AppException::class.java) {
            studentService.enrollStudent(studentId = studentId, courseCode = courseCode)
        }
        assertThat(exception.errorMessage).isEqualTo("A course with  code: $courseCode does not exist.  Cannot complete enrolment")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun unenrollStudentFromCourseSuccess() {
        val student = Student(id = 123456, studentId = "1234", firstName = "Jimmy", lastName = "Hughes")
        val course = Course(id = 55555, courseCode = "BSC-111", courseName = "IT", courseDescription = "Degree in IT")
        student.addCourse(course)
        val studentWithoutCourse = Student(id = 123456, studentId = "1234", firstName = "Jimmy", lastName = "Hughes")

        every {studentRepository.findByStudentId(student.studentId)} returns student
        every {courseRepository.findByCourseCode(course.courseCode)} returns course
        every {studentRepository.save(any())} returns studentWithoutCourse

        val response = studentService.unenrollStudent(studentId = student.studentId, courseCode = course.courseCode)
        assertThat(response.studentId).isEqualTo("1234")
        assertThat(response.firstName).isEqualTo("Jimmy")
        assertThat(response.lastName).isEqualTo("Hughes")
        assertThat(response.courses).isEmpty()
    }

    @Test
    fun unenrollStudentFromCourseStudentNotFound() {
        val studentId = "1234"
        val courseCode = "BSC-123"

        every {studentRepository.findByStudentId(studentId)} throws AppException(statusCode = 404, reason = "A student with student code: $studentId does not exist.  Cannot complete unenrolment")

        val exception = Assertions.assertThrows(AppException::class.java) {
            studentService.enrollStudent(studentId = studentId, courseCode = courseCode)
        }
        assertThat(exception.errorMessage).isEqualTo("A student with student code: $studentId does not exist.  Cannot complete unenrolment")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun unenrollStudentFromCourseCourseNotFound() {
        val studentId = "1234"
        val courseCode = "BSC-123"
        val student = Student(id = 123456, studentId = "1234", firstName = "Jimmy", lastName = "Hughes")

        every {studentRepository.findByStudentId(studentId)} returns student
        every {courseRepository.findByCourseCode(courseCode)} throws AppException(statusCode = 404, reason = "A course with  code: $courseCode does not exist.  Cannot complete unenrolment")

        val exception = Assertions.assertThrows(AppException::class.java) {
            studentService.enrollStudent(studentId = studentId, courseCode = courseCode)
        }
        assertThat(exception.errorMessage).isEqualTo("A course with  code: $courseCode does not exist.  Cannot complete unenrolment")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }
}

