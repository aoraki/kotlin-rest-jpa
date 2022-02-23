package jk.codespace.restapi.repository

import jk.codespace.restapi.entities.Course
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CourseRepository : CrudRepository<Course, Long> {
    fun findByCourseCode(courseCode: String): Course?
}