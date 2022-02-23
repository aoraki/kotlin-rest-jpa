package jk.codespace.restapi.dto

data class CourseDTO(val courseCode: String = "", val courseName: String = "", val courseDescription: String = "", val students: Set<StudentDTO> = mutableSetOf())