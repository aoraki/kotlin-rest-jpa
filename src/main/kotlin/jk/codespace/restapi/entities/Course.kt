package jk.codespace.restapi.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class Course(
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = Int.MIN_VALUE,

    @Column(nullable=false, unique=true)
    var courseCode: String,

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    var students: Set<Student> = mutableSetOf(),

    @JsonBackReference
    @OneToOne(mappedBy = "course", cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    var lecturer: Lecturer? = null,

    var courseName: String,
    var courseDescription: String
) {
    fun removeStudent(student: Student) : Unit{
        var studentSet = students.toMutableSet()
        studentSet.remove(student)
        this.students = studentSet
    }
}