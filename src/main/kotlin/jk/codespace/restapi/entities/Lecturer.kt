package jk.codespace.restapi.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*

@Entity
class Lecturer (
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = Int.MIN_VALUE,

    @Column(nullable=false, unique=true)
    var lecturerId: String,

    @JsonManagedReference
    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinTable(name = "lecturer_course",
        joinColumns = [JoinColumn(name = "lecturer_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "course_id",referencedColumnName = "id")]
    )
    var course: Course? = null,

    var firstName: String,
    var lastName: String
)