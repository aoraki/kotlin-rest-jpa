package jk.codespace.restapi.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class Student (
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = Int.MIN_VALUE,

    @Column(nullable=false, unique=true)
    var studentId: String,

    @ManyToMany(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    @JoinTable(name = "student_course_registrations",
        joinColumns = [JoinColumn(name = "student_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "course_id",referencedColumnName = "id")]
    )
    var courses : Set<Course> = mutableSetOf(),
    var firstName: String,
    var lastName: String
) {
    fun addCourse(course: Course) : Unit{
        var courseSet = courses.toMutableSet()
        courseSet.add(course)
        this.courses = courseSet
    }

    fun removeCourse(course: Course) : Unit{
        var courseSet = courses.toMutableSet()
        courseSet.remove(course)
        this.courses = courseSet
    }
}