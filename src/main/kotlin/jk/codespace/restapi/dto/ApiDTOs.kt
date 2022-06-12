package jk.codespace.restapi.dto

data class CourseDTO(val courseCode: String = "", val courseName: String = "", val courseDescription: String = "", val students: Set<StudentDTOShallow> = mutableSetOf(), var lecturer: LecturerDTOShallow?)
data class StudentDTO(val studentId: String = "", val firstName: String = "", val lastName: String = "", var courses: Set<CourseDTOShallow> = mutableSetOf())
data class LecturerDTO(val lecturerId: String = "", val firstName: String = "", val lastName: String = "", var course: CourseDTOShallow?)

data class StudentDTOShallow(val studentId: String = "", val firstName: String = "", val lastName: String = "")
data class CourseDTOShallow(val courseCode: String = "", val courseName: String = "", val courseDescription: String = "")
data class LecturerDTOShallow(val lecturerId: String = "", val firstName: String = "", val lastName: String = "")