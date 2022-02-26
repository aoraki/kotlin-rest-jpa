package jk.codespace.restapi.service

import io.mockk.every
import io.mockk.mockk
import jk.codespace.restapi.entities.Lecturer
import jk.codespace.restapi.exception.AppException
import jk.codespace.restapi.repository.CourseRepository
import jk.codespace.restapi.repository.LecturerRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class LecturerServiceTest {

    // Mock out the Lecturer Repository
    private val lecturerRepository = mockk<LecturerRepository>()

    // Mock out the Course Repository
    private val courseRepository = mockk<CourseRepository>()

    // Create instance of class under test
    private val lecturerService = LecturerService(lecturerRepository = lecturerRepository, courseRepository = courseRepository)

    @Test
    fun getLecturerSuccess() {
        val lecturerId = "12345"
        val lecturer = Lecturer(id = 12345678, lecturerId = lecturerId, firstName = "Jim", lastName = "Hughes")
        every {lecturerRepository.findByLecturerId(lecturerId)} returns lecturer

        // Invoke the method under test
        val response = lecturerService.getLecturer(lecturerId)

        // Test the response
        assertThat(response?.lecturerId).isEqualTo("12345")
        assertThat(response?.firstName).isEqualTo("Jim")
        assertThat(response?.lastName).isEqualTo("Hughes")
    }

    @Test
    fun getLecturerNotFound() {
        val lecturerId = "12345"
        every {lecturerRepository.findByLecturerId(lecturerId)} throws AppException(statusCode = 404, reason = "Cannot find lecturer with id 1234")

        val exception = Assertions.assertThrows(AppException::class.java) {
            lecturerService.getLecturer(lecturerId)
        }
        assertThat(exception.errorMessage).isEqualTo("Cannot find lecturer with id 1234")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun getAllLecturersSuccess() {
        every {lecturerRepository.findAll()} returns arrayListOf(Lecturer(12345,lecturerId = "123", firstName = "Jay", lastName = "Parker"),Lecturer(67891,lecturerId = "456", firstName = "Pamela", lastName = "Jones"))

        val response = lecturerService.getAllLecturers()

        // Test the response
        assertThat(response[0].lecturerId).isEqualTo("123")
        assertThat(response[0].firstName).isEqualTo("Jay")
        assertThat(response[0].lastName).isEqualTo("Parker")
        assertThat(response[1].lecturerId).isEqualTo("456")
        assertThat(response[1].firstName).isEqualTo("Pamela")
        assertThat(response[1].lastName).isEqualTo("Jones")
    }


    @Test
    fun getAllLecturersNoLecturersInDB() {
        every {lecturerRepository.findAll()} returns emptyList()
        // Invoke the method under test
        val response = lecturerService.getAllLecturers()

        // Test the response
        assertThat(response.size).isEqualTo(0)
    }

    @Test
    fun createLecturerSuccess() {
        val persistedLecturer = Lecturer(id = 123456, lecturerId = "1234", firstName = "Jim", lastName = "Hughes")
        val inputLecturer = Lecturer(lecturerId = "1234", firstName = "Jim", lastName = "Hughes")
        every {lecturerRepository.save(any())} returns persistedLecturer
        every {lecturerRepository.findByLecturerId(inputLecturer.lecturerId)} returns null

        val response = lecturerService.createLecturer(inputLecturer)
        assertThat(response.lecturerId).isEqualTo("1234")
        assertThat(response.firstName).isEqualTo("Jim")
        assertThat(response.lastName).isEqualTo("Hughes")
    }

    @Test
    fun createLecturerAlreadyFound() {
        val existingLecturer = Lecturer(id = 123456, lecturerId = "1234", firstName = "Jim", lastName = "Hughes")
        val inputLecturer = Lecturer(lecturerId = "1234", firstName = "Jim", lastName = "Hughes")
        every {lecturerRepository.findByLecturerId(inputLecturer.lecturerId)} returns existingLecturer

        val exception = Assertions.assertThrows(AppException::class.java) {
            lecturerService.createLecturer(inputLecturer)
        }
        assertThat(exception.errorMessage).isEqualTo("A lecturer with lecturer id: 1234 already exists")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun updateLecturerSuccess() {
        val lecturer = Lecturer(lecturerId = "1234", firstName = "Jimmy", lastName = "Hughes")
        every {lecturerRepository.save(any())} returns Lecturer(id = 123456, lecturerId = "1234", firstName = "Jimmy", lastName = "Hughes")
        every {lecturerRepository.findByLecturerId(lecturer.lecturerId)} returns Lecturer(id = 123456, lecturerId = "1234", firstName = "Jim", lastName = "Hughes")

        val response = lecturerService.updateLecturer(lecturer)
        assertThat(response.lecturerId).isEqualTo("1234")
        assertThat(response.firstName).isEqualTo("Jimmy")
        assertThat(response.lastName).isEqualTo("Hughes")
    }

    @Test
    fun updateLecturerNotFound() {
        val lecturer = Lecturer(lecturerId = "1234", firstName = "Jimmy", lastName = "Hughes")

        every {lecturerRepository.findByLecturerId(lecturer.lecturerId)} throws AppException(statusCode = 404, reason = "A lecturer with lecturer code: ${lecturer.lecturerId} does not exist.  Cannot update")

        val exception = Assertions.assertThrows(AppException::class.java) {
            lecturerService.updateLecturer(lecturer)
        }
        assertThat(exception.errorMessage).isEqualTo("A lecturer with lecturer code: ${lecturer.lecturerId} does not exist.  Cannot update")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun deleteLecturerSuccess() {
        val lecturer = Lecturer(lecturerId = "1234", firstName = "Jimmy", lastName = "Hughes")
        every {lecturerRepository.findByLecturerId(lecturer.lecturerId)} returns Lecturer(id = 123456, lecturerId = "1234", firstName = "Jim", lastName = "Hughes")
        every {lecturerRepository.delete(any())} returns Unit
        val response = lecturerService.deleteLecturer(lecturer.lecturerId)
        assertThat(response).isTrue
    }

    @Test
    fun deleteLecturerNotFound() {
        val lecturer = Lecturer(lecturerId = "1234", firstName = "Jimmy", lastName = "Hughes")
        every {lecturerRepository.findByLecturerId(lecturer.lecturerId)} throws AppException(statusCode = 404, reason = "A lecturer with lecturer code: 1234 does not exist. Cannot delete")
        val exception = Assertions.assertThrows(AppException::class.java) {
            lecturerService.deleteLecturer(lecturer.lecturerId)
        }
        assertThat(exception.errorMessage).isEqualTo("A lecturer with lecturer code: 1234 does not exist. Cannot delete")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun deleteLecturerUnexpectedError() {
        val lecturer = Lecturer(lecturerId = "1234", firstName = "Jimmy", lastName = "Hughes")
        every {lecturerRepository.findByLecturerId(lecturer.lecturerId)} returns Lecturer(id = 123456, lecturerId = "1234", firstName = "Jim", lastName = "Hughes")
        every {lecturerRepository.delete(any())} throws Exception("DB Does not exist")

        val exception = Assertions.assertThrows(AppException::class.java) {
            lecturerService.deleteLecturer(lecturer.lecturerId)
        }
        assertThat(exception.errorMessage).isEqualTo("Unexpected error encountered deleting lecturer with lecturer id 1234")
        assertThat(exception.httpStatus).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
    }

}

