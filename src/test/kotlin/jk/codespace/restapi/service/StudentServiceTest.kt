package jk.codespace.restapi.service

import io.mockk.every
import io.mockk.mockk
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
        val inputStudent = Student(studentId = "1234", firstName = "Jim", lastName = "Hughes")
        every {studentRepository.save(any())} returns persistedStudent
        every {studentRepository.findByStudentId(inputStudent.studentId)} returns null

        val response = studentService.createStudent(inputStudent)
        assertThat(response.studentId).isEqualTo("1234")
        assertThat(response.firstName).isEqualTo("Jim")
        assertThat(response.lastName).isEqualTo("Hughes")
    }

    @Test
    fun createStudentAlreadyFound() {
        val existingStudent = Student(id = 123456, studentId = "1234", firstName = "Jim", lastName = "Hughes")
        val inputStudent = Student(studentId = "1234", firstName = "Jim", lastName = "Hughes")
        every {studentRepository.findByStudentId(inputStudent.studentId)} returns existingStudent

        val exception = Assertions.assertThrows(AppException::class.java) {
            studentService.createStudent(inputStudent)
        }
        assertThat(exception.errorMessage).isEqualTo("A student with student code: 1234 already exists")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun updateStudentSuccess() {
        val student = Student(studentId = "1234", firstName = "Jimmy", lastName = "Hughes")
        every {studentRepository.save(any())} returns Student(id = 123456, studentId = "1234", firstName = "Jimmy", lastName = "Hughes")
        every {studentRepository.findByStudentId(student.studentId)} returns Student(id = 123456, studentId = "1234", firstName = "Jim", lastName = "Hughes")

        val response = studentService.updateStudent(student)
        assertThat(response.studentId).isEqualTo("1234")
        assertThat(response.firstName).isEqualTo("Jimmy")
        assertThat(response.lastName).isEqualTo("Hughes")
    }

    @Test
    fun updateStudentNotFound() {
        val student = Student(studentId = "1234", firstName = "Jimmy", lastName = "Hughes")

        every {studentRepository.findByStudentId(student.studentId)} throws AppException(statusCode = 404, reason = "A student with student code: 1234 does not exist. Cannot update")

        val exception = Assertions.assertThrows(AppException::class.java) {
            studentService.updateStudent(student)
        }
        assertThat(exception.errorMessage).isEqualTo("A student with student code: 1234 does not exist. Cannot update")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun deleteStudentSuccess() {
        val student = Student(studentId = "1234", firstName = "Jimmy", lastName = "Hughes")
        every {studentRepository.findByStudentId(student.studentId)} returns Student(id = 123456, studentId = "1234", firstName = "Jim", lastName = "Hughes")
        every {studentRepository.delete(any())} returns Unit
        val response = studentService.deleteStudent(student.studentId)
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

}

