package jk.codespace.restapi.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import jk.codespace.restapi.entities.Course
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

    @Test
    fun enrollStudentInCourse200Response(){
        val student: Student = generateStudent("2222", "Peter", "Pan")
        val course: Course = generateCourse(courseCode = "3333", courseName = "IT", courseDescription = "Degree in IT")
        student.addCourse(course)

        every {studentService.enrollStudent(studentId = student.studentId, courseCode = course.courseCode)} returns student

        mvc.perform(
            post("/v1/students/{studentid}/courses/{coursecode}", "2222", "3333")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.studentId").value("2222"))
            .andExpect(jsonPath("$.firstName").value("Peter"))
            .andExpect(jsonPath("$.lastName").value("Pan"))
            .andExpect(jsonPath("$.courses.[0].courseCode").value("3333"))
            .andExpect(jsonPath("$.courses.[0].courseName").value("IT"))
            .andExpect(jsonPath("$.courses.[0].courseDescription").value("Degree in IT"))
    }

    @Test
    fun enrollStudentInCourse405Response(){
        mvc.perform(
            put("/v1/students/{studentid}/courses/{coursecode}", "2222", "3333")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed)
    }


    @Test
    fun enrollStudentInCourseStudentNotFound(){
        every {studentService.enrollStudent(studentId = "2222", courseCode = "3333")} throws AppException(statusCode = 404, reason = "A student with student code: 2222 does not exist.  Cannot complete enrolment")

        mvc.perform(
            post("/v1/students/{studentid}/courses/{coursecode}", "2222", "3333")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun enrollStudentInCourseCourseNotFound(){
        every {studentService.enrollStudent(studentId = "2222", courseCode = "3333")} throws AppException(statusCode = 404, reason = "A course with  code: 3333 does not exist.  Cannot complete enrolment")
        mvc.perform(
            post("/v1/students/{studentid}/courses/{coursecode}", "2222", "3333")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun unenrollStudentInCourse200Response(){
        val student: Student = generateStudent("2222", "Peter", "Pan")
        val course: Course = generateCourse(courseCode = "3333", courseName = "IT", courseDescription = "Degree in IT")
        student.addCourse(course)

        every {studentService.unenrollStudent(studentId = student.studentId, courseCode = course.courseCode)} returns student

        mvc.perform(
            delete("/v1/students/{studentid}/courses/{coursecode}", "2222", "3333")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.studentId").value("2222"))
            .andExpect(jsonPath("$.firstName").value("Peter"))
            .andExpect(jsonPath("$.lastName").value("Pan"))
            .andExpect(jsonPath("$.courses.[0].courseCode").value("3333"))
            .andExpect(jsonPath("$.courses.[0].courseName").value("IT"))
            .andExpect(jsonPath("$.courses.[0].courseDescription").value("Degree in IT"))
    }

    @Test
    fun unenrollStudentInCourseStudentNotFound(){
        every {studentService.unenrollStudent(studentId = "2222", courseCode = "3333")} throws AppException(statusCode = 404, reason = "A student with student code: 2222 does not exist.  Cannot complete enrolment")

        mvc.perform(
            delete("/v1/students/{studentid}/courses/{coursecode}", "2222", "3333")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun unenrollStudentInCourseCourseNotFound(){
        every {studentService.unenrollStudent(studentId = "2222", courseCode = "3333")} throws AppException(statusCode = 404, reason = "A course with  code: 3333 does not exist.  Cannot complete enrolment")
        mvc.perform(
            delete("/v1/students/{studentid}/courses/{coursecode}", "2222", "3333")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }



    fun generateStudent(studentId: String, firstName: String, lastName: String) : Student{
        return Student(studentId = studentId, firstName = firstName, lastName = lastName)
    }

    fun generateCourse(courseCode: String, courseName: String, courseDescription: String) : Course {
        return Course(courseCode = courseCode, courseName = courseName, courseDescription = courseDescription)
    }

}