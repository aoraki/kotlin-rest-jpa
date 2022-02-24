package jk.codespace.restapi.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import jk.codespace.restapi.entities.Student
import jk.codespace.restapi.exception.AppException
import jk.codespace.restapi.service.StudentService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(StudentController::class)
class StudentControllerTest {

    @MockkBean
    private lateinit var studentService: StudentService

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun apiGeneralNotFound() {
        mvc.perform(get("/"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun getAllStudentsInvalidHTTPMethod() {
        mvc.perform(put("/v1/students"))
            .andExpect(status().isMethodNotAllowed)
    }

    @Test
    fun getAllStudentsEmptyList200Response() {
        every { studentService.getAllStudents() } returns emptyList()

        mvc.perform(get("/v1/students"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)
    }

    @Test
    fun getAllStudents200ResponseOneStudent() {
        every { studentService.getAllStudents()} returns arrayListOf(generateStudent("123", "Pete", "Parker"))

        mvc.perform(get("/v1/students"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$.[0].studentId").value("123"))
            .andExpect(jsonPath("$.[0].firstName").value("Pete"))
            .andExpect(jsonPath("$.[0].lastName").value("Parker"))
    }

    @Test
    fun getAllStudents200ResponseMultipleStudents() {
        every { studentService.getAllStudents() } returns arrayListOf(generateStudent("111", "Tom", "Thumb"), generateStudent("222", "Peter", "Pan"))

        mvc.perform(get("/v1/students")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$.[0].studentId").value("111"))
            .andExpect(jsonPath("$.[0].firstName").value("Tom"))
            .andExpect(jsonPath("$.[0].lastName").value("Thumb"))
            .andExpect(jsonPath("$.[1].studentId").value("222"))
            .andExpect(jsonPath("$.[1].firstName").value("Peter"))
            .andExpect(jsonPath("$.[1].lastName").value("Pan"))
    }

    @Test
    fun getAllStudents406Response(){
        every { studentService.getAllStudents() } returns emptyList()
        mvc.perform(get("/v1/students")
            .accept(MediaType.APPLICATION_XML_VALUE))
            .andExpect(status().isNotAcceptable)
    }

    @Test
    fun getStudentByStudentIdWrongMethod405Response() {
        mvc.perform(post("/v1/students/{id}", "123"))
            .andExpect(status().isMethodNotAllowed)
    }

    @Test
    fun getStudentByStudentIdSuccessfulResponse(){
        every { studentService.getStudent("123") } returns generateStudent("123", "Sam", "Tracey")

        mvc.perform(get("/v1/students/{id}", "123"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.studentId").value("123"))
            .andExpect(jsonPath("$.firstName").value("Sam"))
            .andExpect(jsonPath("$.lastName").value("Tracey"))
    }


    @Test
    fun getStudentByStudentIdNotFound(){
        every { studentService.getStudent("123") } throws AppException(statusCode = 404, reason = "Cannot find student with id 123")

        mvc.perform(get("/v1/students/{id}", "123"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun createStudentSuccessful201Response(){
        every {studentService.createStudent(any())} returns generateStudent("2222", "Peter", "Pan")

        val payload = """
                {
                    "studentId": "2222",
                    "firstName": "Peter",
                    "lastName": "Pan"
                }
        """.trimIndent()

        mvc.perform(
            post("/v1/students")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.studentId").value("2222"))
            .andExpect(jsonPath("$.firstName").value("Peter"))
            .andExpect(jsonPath("$.lastName").value("Pan"))
    }

    @Test
    fun createStudent409Conflict(){
        every { studentService.createStudent(any()) } throws AppException(statusCode = 409, reason = "A student with student code: 2222 already exists")

        val payload = """
                {
                    "studentId": "2222",
                    "firstName": "Peter",
                    "lastName": "Pan"
                }
        """.trimIndent()

        mvc.perform(
            post("/v1/students")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isConflict)
    }

    @Test
    fun createStudentReturn415(){
        val payload = """
                {
                    "studentId": "2222",
                    "firstName": "Peter",
                    "lastName": "Pan"
                }
        """.trimIndent()

        mvc.perform(
            post("/v1/students")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_XML)
                .content(payload))
            .andExpect(status().isUnsupportedMediaType)
    }

    @Test
    fun updateStudentSuccessful200Response(){
        every { studentService.updateStudent(any()) } returns generateStudent("2222", "Peter", "Pan")

        val payload = """
                {
                    "studentId": "2222",
                    "firstName": "Peter",
                    "lastName": "Pan"
                }
        """.trimIndent()

        mvc.perform(
            patch("/v1/students")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk)
            .andExpect(jsonPath("studentId").value("2222"))
            .andExpect(jsonPath("firstName").value("Peter"))
            .andExpect(jsonPath("lastName").value("Pan"))
    }

    @Test
    fun updateStudentNotFoundResponse(){
        every {studentService.updateStudent(any())} throws AppException(statusCode = 404, reason = "Cannot find student with id 2222")

        val payload = """
                {
                    "studentId": "2222",
                    "firstName": "Peter",
                    "lastName": "Pan"
                }
        """.trimIndent()

        mvc.perform(
            patch("/v1/students")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isNotFound)
    }

    @Test
    fun updateStudentReturn415(){
        val payload = """
                {
                    "studentId": "2222",
                    "firstName": "Peter",
                    "lastName": "Pan"
                }
        """.trimIndent()

        mvc.perform(
            patch("/v1/students")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_XML)
                .content(payload))
            .andExpect(status().isUnsupportedMediaType)
    }

    @Test
    fun deleteStudentSuccessfulResponse(){
        every { studentService.deleteStudent("123") } returns true
        mvc.perform(delete("/v1/students/{id}", "123"))
            .andExpect(status().isOk)
    }

    @Test
    fun deleteStudentNotFoundResponse(){
        every {studentService.deleteStudent("123")} throws AppException(statusCode = 404, reason = "Cannot find student with id 123")
        mvc.perform(delete("/v1/students/{id}", "123"))
            .andExpect(status().isNotFound)
    }


    fun generateStudent(studentId: String, firstName: String, lastName: String) : Student{
        return Student(studentId = studentId, firstName = firstName, lastName = lastName)
    }
}