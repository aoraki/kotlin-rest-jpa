package jk.codespace.restapi.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class Course (
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = Int.MIN_VALUE,

    @Column(nullable=false, unique=true)
    var courseCode: String,

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    var students: Set<Student> = mutableSetOf(),

    var courseName: String,
    var courseDescription: String
) {

    fun addStudent(student: Student) : Unit{
        var studentSet = students.toMutableSet()
        studentSet.add(student)
        this.students = studentSet
    }

    fun removeStudent(student: Student) : Unit{
        var studentSet = students.toMutableSet()
        studentSet.remove(student)
        this.students = studentSet
    }
}