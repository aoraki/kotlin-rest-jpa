package jk.codespace.restapi.utils

import jk.codespace.restapi.dto.CourseDTO
import jk.codespace.restapi.dto.StudentDTO
import jk.codespace.restapi.entities.Course
import jk.codespace.restapi.entities.Student

class Conversion {

    fun convertStudentToDTO(student: Student): StudentDTO {
        return StudentDTO(studentId = student.studentId, firstName = student.firstName, lastName = student.lastName, courses = convertCourseSetToDTOSet(student.courses))
    }

    fun convertStudentDTOToStudent(studentDTO: StudentDTO) : Student {
        return Student(studentId = studentDTO.studentId, firstName = studentDTO.firstName, lastName = studentDTO.lastName)
    }

    fun convertStudentDTOToStudentWithDatabaseId(studentDTO: StudentDTO, databaseIdentifier: Int) : Student {
        return Student(id = databaseIdentifier, studentId = studentDTO.studentId, firstName = studentDTO.firstName, lastName = studentDTO.lastName)
    }

    fun convertCourseToDTO(course: Course): CourseDTO {
        return CourseDTO(courseCode = course.courseCode, courseName = course.courseName, courseDescription = course.courseDescription, students = convertStudentSetToDTOSet(course.students))
    }

    fun convertCourseSetToDTOSet(courseSet: Set<Course>): Set<CourseDTO> {
        var courseDTOSet = mutableSetOf<CourseDTO>()
        courseSet.forEach {courseDTOSet.add(convertCourseToDTO(it))}
        return courseDTOSet
    }

    fun convertStudentSetToDTOSet(studentSet: Set<Student>): Set<StudentDTO> {
        var studentDTOSet = mutableSetOf<StudentDTO>()
        studentSet.forEach {studentDTOSet.add(convertStudentToDTO(it))}
        return studentDTOSet
    }

    fun convertCourseDTOToCourse(courseDTO: CourseDTO) : Course {
        return Course(courseCode = courseDTO.courseCode, courseName = courseDTO.courseName, courseDescription = courseDTO.courseDescription)
    }

    fun convertCourseDTOToCourseWithDatabaseId(courseDTO: CourseDTO, databaseIdentifier: Int) : Course {
        return Course(id = databaseIdentifier, courseCode = courseDTO.courseCode, courseName = courseDTO.courseName, courseDescription = courseDTO.courseDescription)
    }
}