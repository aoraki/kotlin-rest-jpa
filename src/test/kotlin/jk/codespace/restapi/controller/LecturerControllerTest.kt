package jk.codespace.restapi.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import jk.codespace.restapi.entities.Course
import jk.codespace.restapi.entities.Lecturer
import jk.codespace.restapi.exception.AppException
import jk.codespace.restapi.service.LecturerService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(LecturerController::class)
class LecturerControllerTest {

    @MockkBean
    private lateinit var lecturerService: LecturerService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun apiGeneralNotFound() {
        mvc.perform(get("/"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun getAllLecturersInvalidHTTPMethod() {
        mvc.perform(put("/v1/lecturers"))
            .andExpect(status().isMethodNotAllowed)
    }

    @Test
    fun getAllLecturersEmptyList200Response() {
        every { lecturerService.getAllLecturers() } returns emptyList()

        mvc.perform(get("/v1/lecturers"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)
    }

    @Test
    fun getAllLecturers200ResponseOneLecturer() {
        every { lecturerService.getAllLecturers()} returns arrayListOf(generateLecturer("123", "Pete", "Parker"))

        mvc.perform(get("/v1/lecturers"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$.[0].lecturerId").value("123"))
            .andExpect(jsonPath("$.[0].firstName").value("Pete"))
            .andExpect(jsonPath("$.[0].lastName").value("Parker"))
    }

    @Test
    fun getAllLecturers200ResponseMultipleLecturers() {
        every { lecturerService.getAllLecturers() } returns arrayListOf(generateLecturer("111", "Tom", "Thumb"), generateLecturer("222", "Peter", "Pan"))

        mvc.perform(get("/v1/lecturers")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$.[0].lecturerId").value("111"))
            .andExpect(jsonPath("$.[0].firstName").value("Tom"))
            .andExpect(jsonPath("$.[0].lastName").value("Thumb"))
            .andExpect(jsonPath("$.[1].lecturerId").value("222"))
            .andExpect(jsonPath("$.[1].firstName").value("Peter"))
            .andExpect(jsonPath("$.[1].lastName").value("Pan"))
    }

    @Test
    fun getAllLecturers406Response(){
        every { lecturerService.getAllLecturers() } returns emptyList()
        mvc.perform(get("/v1/lecturers")
            .accept(MediaType.APPLICATION_XML_VALUE))
            .andExpect(status().isNotAcceptable)
    }

    @Test
    fun getLecturerByLecturerIdWrongMethod405Response() {
        mvc.perform(post("/v1/lecturers/{id}", "123"))
            .andExpect(status().isMethodNotAllowed)
    }

    @Test
    fun getLecturerByLecturerIdSuccessfulResponse(){
        every { lecturerService.getLecturer("123") } returns generateLecturer("123", "Sam", "Tracey")

        mvc.perform(get("/v1/lecturers/{id}", "123"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.lecturerId").value("123"))
            .andExpect(jsonPath("$.firstName").value("Sam"))
            .andExpect(jsonPath("$.lastName").value("Tracey"))
    }


    @Test
    fun getLecturerByLecturerIdNotFound(){
        every { lecturerService.getLecturer("123") } throws AppException(statusCode = 404, reason = "Cannot find lecturer with id 123")

        mvc.perform(get("/v1/lecturers/{id}", "123"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun createLecturerSuccessful201Response(){
        every {lecturerService.createLecturer(any())} returns generateLecturer("2222", "Peter", "Pan")

        val payload = """
                {
                    "lecturerId": "2222",
                    "firstName": "Peter",
                    "lastName": "Pan"
                }
        """.trimIndent()

        mvc.perform(
            post("/v1/lecturers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.lecturerId").value("2222"))
            .andExpect(jsonPath("$.firstName").value("Peter"))
            .andExpect(jsonPath("$.lastName").value("Pan"))
    }

    @Test
    fun createLecturer409Conflict(){
        every { lecturerService.createLecturer(any()) } throws AppException(statusCode = 409, reason = "A lecturer with lecturer code: 2222 already exists")

        val payload = """
                {
                    "lecturerId": "2222",
                    "firstName": "Peter",
                    "lastName": "Pan"
                }
        """.trimIndent()

        mvc.perform(
            post("/v1/lecturers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isConflict)
    }

    @Test
    fun createLecturerReturn415(){
        val payload = """
                {
                    "lecturerId": "2222",
                    "firstName": "Peter",
                    "lastName": "Pan"
                }
        """.trimIndent()

        mvc.perform(
            post("/v1/lecturers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_XML)
                .content(payload))
            .andExpect(status().isUnsupportedMediaType)
    }

    @Test
    fun updateLecturerSuccessful200Response(){
        every { lecturerService.updateLecturer(any()) } returns generateLecturer("2222", "Peter", "Pan")

        val payload = """
                {
                    "lecturerId": "2222",
                    "firstName": "Peter",
                    "lastName": "Pan"
                }
        """.trimIndent()

        mvc.perform(
            patch("/v1/lecturers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk)
            .andExpect(jsonPath("lecturerId").value("2222"))
            .andExpect(jsonPath("firstName").value("Peter"))
            .andExpect(jsonPath("lastName").value("Pan"))
    }

    @Test
    fun updateLecturerNotFoundResponse(){
        every {lecturerService.updateLecturer(any())} throws AppException(statusCode = 404, reason = "Cannot find lecturer with id 2222")

        val payload = """
                {
                    "lecturerId": "2222",
                    "firstName": "Peter",
                    "lastName": "Pan"
                }
        """.trimIndent()

        mvc.perform(
            patch("/v1/lecturers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isNotFound)
    }

    @Test
    fun updateLecturerReturn415(){
        val payload = """
                {
                    "lecturerId": "2222",
                    "firstName": "Peter",
                    "lastName": "Pan"
                }
        """.trimIndent()

        mvc.perform(
            patch("/v1/lecturers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_XML)
                .content(payload))
            .andExpect(status().isUnsupportedMediaType)
    }

    @Test
    fun deleteLecturerSuccessfulResponse(){
        every { lecturerService.deleteLecturer("123") } returns true
        mvc.perform(delete("/v1/lecturers/{id}", "123"))
            .andExpect(status().isOk)
    }

    @Test
    fun deleteLecturerNotFoundResponse(){
        every {lecturerService.deleteLecturer("123")} throws AppException(statusCode = 404, reason = "Cannot find lecturer with id 123")
        mvc.perform(delete("/v1/lecturers/{id}", "123"))
            .andExpect(status().isNotFound)
    }


    @Test
    fun enrollLecturerInCourse200Response(){
        val lecturer: Lecturer = generateLecturer("2222", "Peter", "Pan")
        val course: Course = generateCourse(courseCode = "3333", courseName = "IT", courseDescription = "Degree in IT")
        lecturer.course = course

        every {lecturerService.assignLecturer(lecturerId = lecturer.lecturerId, courseCode = course.courseCode)} returns lecturer

        mvc.perform(
            post("/v1/lecturers/{lecturerid}/courses/{coursecode}", "2222", "3333")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.lecturerId").value("2222"))
            .andExpect(jsonPath("$.firstName").value("Peter"))
            .andExpect(jsonPath("$.lastName").value("Pan"))
            .andExpect(jsonPath("$.course.courseCode").value("3333"))
            .andExpect(jsonPath("$.course.courseName").value("IT"))
            .andExpect(jsonPath("$.course.courseDescription").value("Degree in IT"))
    }

    @Test
    fun enrollLecturerInCourse405Response(){
        mvc.perform(
            put("/v1/lecturers/{lecturerid}/courses/{coursecode}", "2222", "3333")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed)
    }


    @Test
    fun enrollLecturerInCourseLecturerNotFound(){
        every {lecturerService.assignLecturer(lecturerId = "2222", courseCode = "3333")} throws AppException(statusCode = 404, reason = "A lecturer with lecturer code: 2222 does not exist.  Cannot complete enrolment")

        mvc.perform(
            post("/v1/lecturers/{lecturerid}/courses/{coursecode}", "2222", "3333")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun enrollLecturerInCourseCourseNotFound(){
        every {lecturerService.assignLecturer(lecturerId = "2222", courseCode = "3333")} throws AppException(statusCode = 404, reason = "A course with  code: 3333 does not exist.  Cannot complete enrolment")
        mvc.perform(
            post("/v1/lecturers/{lecturerid}/courses/{coursecode}", "2222", "3333")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun unenrollLecturerInCourse200Response(){
        val lecturer: Lecturer = generateLecturer("2222", "Peter", "Pan")
        val courseCode = "4445"
        lecturer.course = null

        every {lecturerService.deassignLecturer(lecturerId = lecturer.lecturerId, courseCode = courseCode)} returns lecturer

        mvc.perform(
            delete("/v1/lecturers/{lecturerid}/courses/{coursecode}", "2222", "4445")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.lecturerId").value("2222"))
            .andExpect(jsonPath("$.firstName").value("Peter"))
            .andExpect(jsonPath("$.lastName").value("Pan"))
            .andExpect(jsonPath("$.course").isEmpty)
     }


    @Test
    fun unenrollLecturerInCourseLecturerNotFound(){
        every {lecturerService.deassignLecturer(lecturerId = "2222", courseCode = "3333")} throws AppException(statusCode = 404, reason = "A lecturer with lecturer code: 2222 does not exist.  Cannot complete enrolment")

        mvc.perform(
            delete("/v1/lecturers/{lecturerid}/courses/{coursecode}", "2222", "3333")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun unenrollLecturerInCourseCourseNotFound(){
        every {lecturerService.deassignLecturer(lecturerId = "2222", courseCode = "3333")} throws AppException(statusCode = 404, reason = "A course with  code: 3333 does not exist.  Cannot complete enrolment")
        mvc.perform(
            delete("/v1/lecturers/{lecturerid}/courses/{coursecode}", "2222", "3333")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }


    fun generateLecturer(lecturerId: String, firstName: String, lastName: String) : Lecturer{
        return Lecturer(lecturerId = lecturerId, firstName = firstName, lastName = lastName)
    }

    fun generateCourse(courseCode: String, courseName: String, courseDescription: String) : Course {
        return Course(courseCode = courseCode, courseName = courseName, courseDescription = courseDescription)
    }
}