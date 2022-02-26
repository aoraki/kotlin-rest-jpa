package jk.codespace.restapi.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class Lecturer (
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = Int.MIN_VALUE,

    @Column(nullable=false, unique=true)
    var lecturerId: String,

    var firstName: String,
    var lastName: String
)