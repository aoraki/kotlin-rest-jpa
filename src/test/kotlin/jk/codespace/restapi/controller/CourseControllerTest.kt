package jk.codespace.restapi.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import jk.codespace.restapi.entities.Course
import jk.codespace.restapi.entities.Student
import jk.codespace.restapi.exception.AppException
import jk.codespace.restapi.service.CourseService
import jk.codespace.restapi.service.StudentService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(CourseController::class)
class CourseControllerTest {

    @MockkBean
    private lateinit var courseService: CourseService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun getAllCoursesInvalidHTTPMethod() {
        mvc.perform(MockMvcRequestBuilders.put("/v1/courses"))
            .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed)
    }

    @Test
    fun getAllCoursesEmptyList200Response() {
        every { courseService.getAllCourses() } returns emptyList()

        mvc.perform(MockMvcRequestBuilders.get("/v1/courses"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty)
    }

    @Test
    fun getAllCourses200ResponseOneCourse() {
        every { courseService.getAllCourses() } returns arrayListOf(generateCourse("123", "Medicine", "Degree in Medicine"))

        mvc.perform(MockMvcRequestBuilders.get("/v1/courses"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courseCode").value("123"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courseName").value("Medicine"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courseDescription").value("Degree in Medicine"))
    }

    @Test
    fun getAllCourses200ResponseMultipleCourses() {
        every { courseService.getAllCourses() } returns arrayListOf(generateCourse("111", "Medicine", "Degree in Medicine"), generateCourse("222", "Science", "Degree in Science"))

        mvc.perform(
            MockMvcRequestBuilders.get("/v1/courses")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courseCode").value("111"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courseName").value("Medicine"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].courseDescription").value("Degree in Medicine"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].courseCode").value("222"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].courseName").value("Science"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].courseDescription").value("Degree in Science"))
    }

    @Test
    fun getAllCourses406Response(){
        every { courseService.getAllCourses() } returns emptyList()
        mvc.perform(
            MockMvcRequestBuilders.get("/v1/courses")
            .accept(MediaType.APPLICATION_XML_VALUE))
            .andExpect(MockMvcResultMatchers.status().isNotAcceptable)
    }

    @Test
    fun getCourseByCourseIdWrongMethod405Response() {
        mvc.perform(MockMvcRequestBuilders.post("/v1/courses/{id}", "123"))
            .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed)
    }

    @Test
    fun getCourseByCourseIdSuccessfulResponse(){
        every { courseService.getCourse("123") } returns generateCourse("123", "Science", "Degree in Science")

        mvc.perform(MockMvcRequestBuilders.get("/v1/courses/{id}", "123"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.courseCode").value("123"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.courseName").value("Science"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.courseDescription").value("Degree in Science"))
    }

    @Test
    fun getCourseByCourseCodeNotFound(){
        every { courseService.getCourse("123") } throws AppException(statusCode = 404, reason = "Cannot find Course with code 123")

        mvc.perform(MockMvcRequestBuilders.get("/v1/courses/{id}", "123"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun createCourseSuccessful201Response(){
        every { courseService.createCourse(any())} returns generateCourse("2222", "Pharmacy", "Degree in Pharmacy")

        val payload = """
                {
                    "studentId": "2222",
                    "firstName": "Pharmacy",
                    "lastName": "Degree in Pharmacy"
                }
        """.trimIndent()

        mvc.perform(
            MockMvcRequestBuilders.post("/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.courseCode").value("2222"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.courseName").value("Pharmacy"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.courseDescription").value("Degree in Pharmacy"))
    }

    @Test
    fun createCourse409Conflict(){
        every { courseService.createCourse(any()) } throws AppException(statusCode = 409, reason = "A course with course code: 2222 already exists")

        val payload = """
                {
                    "studentId": "2222",
                    "firstName": "Pharmacy",
                    "lastName": "Degree in Pharmacy"
                }
        """.trimIndent()

        mvc.perform(
            MockMvcRequestBuilders.post("/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(MockMvcResultMatchers.status().isConflict)
    }

    @Test
    fun createCourseReturn415(){
        val payload = """
                {
                    "studentId": "2222",
                    "firstName": "Pharmacy",
                    "lastName": "Degree in Pharmacy"
                }
        """.trimIndent()

        mvc.perform(
            MockMvcRequestBuilders.post("/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_XML)
                .content(payload))
            .andExpect(MockMvcResultMatchers.status().isUnsupportedMediaType)
    }

    @Test
    fun updateCourseSuccessful200Response(){
        every { courseService.updateCourse(any()) } returns generateCourse("2222", "IT", "Degree in IT")

        val payload = """
                {
                    "courseCode": "2222",
                    "courseName": "II",
                    "courseDescription": "Degree in IT"
                }
        """.trimIndent()

        mvc.perform(
            MockMvcRequestBuilders.patch("/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("courseCode").value("2222"))
            .andExpect(MockMvcResultMatchers.jsonPath("courseName").value("IT"))
            .andExpect(MockMvcResultMatchers.jsonPath("courseDescription").value("Degree in IT"))
    }

    @Test
    fun updateCourseNotFoundResponse(){
        every {courseService.updateCourse(any())} throws AppException(statusCode = 404, reason = "Cannot find course with code 2222")

        val payload = """
                {
                    "studentId": "2222",
                    "firstName": "IT",
                    "lastName": "Degree in IT"
                }
        """.trimIndent()

        mvc.perform(
            MockMvcRequestBuilders.patch("/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun updateCourseReturn415(){
        val payload = """
                {
                    "studentId": "2222",
                    "firstName": "IT",
                    "lastName": "Degree in IT"
                }
        """.trimIndent()

        mvc.perform(
            MockMvcRequestBuilders.patch("/v1/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_XML)
                .content(payload))
            .andExpect(MockMvcResultMatchers.status().isUnsupportedMediaType)
    }

    @Test
    fun deleteCourseSuccessfulResponse(){
        every { courseService.deleteCourse("123") } returns true
        mvc.perform(MockMvcRequestBuilders.delete("/v1/courses/{id}", "123"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun deleteCourseNotFoundResponse(){
        every { courseService.deleteCourse("123") } throws AppException(statusCode = 404, reason = "Cannot find course with code 123")
        mvc.perform(MockMvcRequestBuilders.delete("/v1/courses/{id}", "123"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    fun generateCourse(courseCode: String, courseName: String, courseDescription: String) : Course {
        return Course(courseCode = courseCode, courseName = courseName, courseDescription = courseDescription)
    }
}