package jk.codespace.restapi.service

import jk.codespace.restapi.entities.Course
import jk.codespace.restapi.entities.Student
import jk.codespace.restapi.exception.AppException
import jk.codespace.restapi.repository.CourseRepository
import jk.codespace.restapi.repository.StudentRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class StudentService(
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository) {

    private val log = KotlinLogging.logger {}

    fun getStudent(studentId: String?): Student? {
        val student: Student? = studentRepository.findByStudentId(studentId!!)
        if(student != null) return student else throw AppException(statusCode = 404, reason = "Cannot find student with id $studentId")
    }

    fun getAllStudents(): List<Student> {
        log.info { "Attempting to get all students" }
        val students = studentRepository.findAll() as List<Student>
        return students
    }

    fun createStudent(student: Student): Student {
        log.info { "Attempting to create Student with Id : ${student.studentId}" }

        val checkStudent: Student? = studentRepository.findByStudentId(student.studentId)
        if(checkStudent != null){
            throw AppException(statusCode = 409, reason = "A student with student code: ${student.studentId} already exists")
        }

        val retStudent: Student = studentRepository.save(student)
        return retStudent
    }

    fun updateStudent(student: Student): Student {
        log.info { "Attempting to update Student with Id : ${student.studentId}" }
        val existingStudent: Student = studentRepository.findByStudentId(student.studentId) ?: throw AppException(statusCode = 404, reason = "A student with student code: ${student.studentId} does not exist.  Cannot update")
        student.id = existingStudent.id
        val retStudent: Student = studentRepository.save(student)
        return retStudent
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

    fun enrollStudent(studentId: String, courseCode: String): Student {
        log.info { "Attempting to enroll Student with Id : ${studentId} in course with code : ${courseCode}"}
        val student: Student = studentRepository.findByStudentId(studentId)  ?: throw AppException(statusCode = 404, reason = "A student with student code: $studentId does not exist.  Cannot complete enrolment")
        val course: Course = courseRepository.findByCourseCode(courseCode)  ?: throw AppException(statusCode = 404, reason = "A course with  code: $courseCode does not exist.  Cannot complete enrolment")
        student.addCourse(course)
        val retStudent: Student = studentRepository.save(student)
        return retStudent
    }

    fun unenrollStudent(studentId: String, courseCode: String): Student {
        log.info { "Attempting to unenroll Student with Id : ${studentId} in course with code : ${courseCode}"}
        val student: Student = studentRepository.findByStudentId(studentId)  ?: throw AppException(statusCode = 404, reason = "A student with student code: $studentId does not exist.  Cannot complete unenrolment")
        val course: Course = courseRepository.findByCourseCode(courseCode)  ?: throw AppException(statusCode = 404, reason = "A course with  code: $courseCode does not exist.  Cannot complete unenrolment")
        student.removeCourse(course)
        val retStudent: Student = studentRepository.save(student)
        return retStudent
    }
}