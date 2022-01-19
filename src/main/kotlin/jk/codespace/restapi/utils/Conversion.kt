package jk.codespace.restapi.utils

import jk.codespace.restapi.dto.StudentDTO
import jk.codespace.restapi.entities.Student
import org.springframework.data.jpa.domain.AbstractPersistable_.id

class Conversion {
    fun convertStudentToDTO(student: Student): StudentDTO {
        return StudentDTO(studentId = student.studentId, firstName = student.firstName, lastName = student.lastName)
    }

    fun convertStudentDTOToStudent(studentDTO: StudentDTO) : Student {
        return Student(studentId = studentDTO.studentId, firstName = studentDTO.firstName, lastName = studentDTO.lastName)
    }

    fun convertStudentDTOToStudentWithDatabaseId(studentDTO: StudentDTO, databaseIdentifier: Int) : Student {
        return Student(id = databaseIdentifier, studentId = studentDTO.studentId, firstName = studentDTO.firstName, lastName = studentDTO.lastName)
    }

}