package jk.codespace.restapi.service

import jk.codespace.restapi.dto.StudentDTO
import jk.codespace.restapi.entities.Course
import jk.codespace.restapi.entities.Student
import jk.codespace.restapi.exception.AppException
import jk.codespace.restapi.repository.CourseRepository
import jk.codespace.restapi.repository.StudentRepository
import jk.codespace.restapi.utils.Conversion
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class StudentService(
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository) {

    private val log = KotlinLogging.logger {}
    private val conversion = Conversion()

    fun getStudent(studentId: String?): StudentDTO? {
        val student: Student? = studentRepository.findByStudentId(studentId!!)
        if(student != null) return conversion.convertStudentToDTO(studentRepository.findByStudentId(studentId!!)!!) else throw AppException(statusCode = 404, reason = "Cannot find student with id $studentId")
    }

    fun getAllStudents(): List<StudentDTO> {
        log.info { "Attempting to get all students" }
        val students = studentRepository.findAll() as List<Student>
        val studentDTOs = ArrayList<StudentDTO>()
        for(student in students){
            studentDTOs.add(conversion.convertStudentToDTO(student))
        }
        return studentDTOs
    }

    fun createStudent(studentDTO: StudentDTO): StudentDTO {
        log.info { "Attempting to create Student with Id : ${studentDTO.studentId}" }

        val checkStudent: Student? = studentRepository.findByStudentId(studentDTO.studentId)
        if(checkStudent != null){
            throw AppException(statusCode = 409, reason = "A student with student code: ${studentDTO.studentId} already exists")
        }

        val student: Student = conversion.convertStudentDTOToStudent(studentDTO)
        val retStudent: Student = studentRepository.save(student)
        return conversion.convertStudentToDTO(retStudent)
    }

    fun updateStudent(studentDTO: StudentDTO): StudentDTO {
        log.info { "Attempting to update Student with Id : ${studentDTO.studentId}" }
        val student: Student = studentRepository.findByStudentId(studentDTO.studentId) ?: throw AppException(statusCode = 404, reason = "A student with student code: ${studentDTO.studentId} does not exist.  Cannot update")

        val updatedStudent: Student = conversion.convertStudentDTOToStudentWithDatabaseId(studentDTO, student.id)
        val retStudent: Student = studentRepository.save(updatedStudent)
        return conversion.convertStudentToDTO(retStudent)
    }

    fun deleteStudent(studentId: String): Boolean {
        log.info { "Attempting to delete Student with Id : ${studentId}" }

        val student: Student = studentRepository.findByStudentId(studentId)  ?: throw AppException(statusCode = 404, reason = "A student with student code: $studentId does not exist.  Cannot delete")
        val courses: Set<Course> = student.courses

        // Ensure courses that the student is currently enrolled in are disassociated from the student
        for(course in courses){
            course.removeStudent(student)
            courseRepository.save(course)
        }

        try {
            studentRepository.delete(student)
        } catch (ex: Exception) {
            throw AppException(500, "Unexpected error encountered deleting student with student id $studentId")
        }
        return true
    }

    fun enrollStudent(studentId: String, courseCode: String): StudentDTO {
        log.info { "Attempting to enroll Student with Id : ${studentId} in course with code : ${courseCode}"}

        val student: Student = studentRepository.findByStudentId(studentId)  ?: throw AppException(statusCode = 404, reason = "A student with student code: $studentId does not exist.  Cannot complete enrolment")
        val course: Course = courseRepository.findByCourseCode(courseCode)  ?: throw AppException(statusCode = 404, reason = "A course with  code: $courseCode does not exist.  Cannot complete enrolment")

        student.addCourse(course)

        //val courseSet = student.courses.toMutableSet()
        //courseSet.add(course)

        //val updatedStudent = Student(id=student.id, studentId = student.studentId, firstName = student.firstName, lastName = student.lastName, courses = courseSet)
        val retStudent: Student = studentRepository.save(student)
        return conversion.convertStudentToDTO(retStudent)
    }

    fun unenrollStudent(studentId: String, courseCode: String): StudentDTO {
        log.info { "Attempting to unenroll Student with Id : ${studentId} in course with code : ${courseCode}"}

        val student: Student = studentRepository.findByStudentId(studentId)  ?: throw AppException(statusCode = 404, reason = "A student with student code: $studentId does not exist.  Cannot complete unenrolment")
        val course: Course = courseRepository.findByCourseCode(courseCode)  ?: throw AppException(statusCode = 404, reason = "A course with  code: $courseCode does not exist.  Cannot complete unenrolment")

        student.removeCourse(course)

        //val courseSet = student.courses.toMutableSet()
        //courseSet.add(course)

        //val updatedStudent = Student(id=student.id, studentId = student.studentId, firstName = student.firstName, lastName = student.lastName, courses = courseSet)
        val retStudent: Student = studentRepository.save(student)
        return conversion.convertStudentToDTO(retStudent)
    }



}