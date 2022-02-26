package jk.codespace.restapi.repository

import jk.codespace.restapi.entities.Lecturer
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LecturerRepository : CrudRepository<Lecturer, Long> {
    fun findByLecturerId(studentId : String): Lecturer?
}