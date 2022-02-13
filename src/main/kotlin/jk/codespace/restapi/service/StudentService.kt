package jk.codespace.restapi.service

import jk.codespace.restapi.dto.StudentDTO
import jk.codespace.restapi.entities.Student
import jk.codespace.restapi.exception.AppException
import jk.codespace.restapi.repository.StudentRepository
import jk.codespace.restapi.utils.Conversion
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class StudentService(
    private val studentRepository: StudentRepository) {

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

        try {
            studentRepository.delete(student)
        } catch (ex: Exception) {
            throw AppException(500, "Unexpected error encountered deleting student with student id $studentId")
        }
        return true
    }
}