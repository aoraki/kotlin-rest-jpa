package jk.codespace.restapi.dto

data class StudentDTO(val studentId: String = "", val firstName: String = "", val lastName: String = "", val courses: Set<CourseDTO> = mutableSetOf())