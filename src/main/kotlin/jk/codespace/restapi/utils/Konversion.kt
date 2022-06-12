package jk.codespace.restapi.utils

import jk.codespace.restapi.dto.*
import jk.codespace.restapi.entities.Course
import jk.codespace.restapi.entities.Lecturer
import jk.codespace.restapi.entities.Student

class Konversion {

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
        return CourseDTO(courseCode = course.courseCode, courseName = course.courseName, courseDescription = course.courseDescription, students = convertStudentSetToDTOSet(course.students), lecturer = course.lecturer?.let {
            convertLecturerToDTOShallow(it)
        })
    }

    fun convertCourseToDTOShallow(course: Course): CourseDTOShallow {
        return CourseDTOShallow(courseCode = course.courseCode, courseName = course.courseName, courseDescription = course.courseDescription)
    }

    fun convertLecturerToDTOShallow(lecturer: Lecturer): LecturerDTOShallow {
        return LecturerDTOShallow(lecturerId = lecturer.lecturerId, firstName = lecturer.firstName, lastName = lecturer.lastName)
    }

    fun convertStudentToDTOShallow(student: Student): StudentDTOShallow {
        return StudentDTOShallow(studentId = student.studentId, firstName = student.firstName, lastName = student.lastName)
    }

    fun convertLecturerToDTO(lecturer: Lecturer): LecturerDTO {
        return LecturerDTO(lecturerId = lecturer.lecturerId, firstName = lecturer.firstName, lastName = lecturer.lastName, course = lecturer.course?.let {
            convertCourseToDTOShallow(it)
        })
    }

    fun convertLecturerDTOToLecturer(lecturerDTO: LecturerDTO) : Lecturer {
        return Lecturer(lecturerId = lecturerDTO.lecturerId, firstName = lecturerDTO.firstName, lastName = lecturerDTO.lastName)
    }

    fun convertLecturerDTOToLecturerWithDatabaseId(lecturerDTO: LecturerDTO, databaseIdentifier: Int) : Lecturer {
        return Lecturer(id = databaseIdentifier, lecturerId = lecturerDTO.lecturerId, firstName = lecturerDTO.firstName, lastName = lecturerDTO.lastName)
    }

    fun convertCourseSetToDTOSet(courseSet: Set<Course>): Set<CourseDTOShallow> {
        var courseDTOSet = mutableSetOf<CourseDTOShallow>()
        courseSet.forEach {courseDTOSet.add(convertCourseToDTOShallow(it))}
        return courseDTOSet
    }

    fun convertStudentSetToDTOSet(studentSet: Set<Student>): Set<StudentDTOShallow> {
        var studentDTOSet = mutableSetOf<StudentDTOShallow>()
        studentSet.forEach {studentDTOSet.add(convertStudentToDTOShallow(it))}
        return studentDTOSet
    }

    fun convertCourseDTOShallowToCourse(courseDTO: CourseDTOShallow) : Course {
        return Course(courseCode = courseDTO.courseCode, courseName = courseDTO.courseName, courseDescription = courseDTO.courseDescription)
    }
}