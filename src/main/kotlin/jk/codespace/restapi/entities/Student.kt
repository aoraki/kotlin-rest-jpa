package jk.codespace.restapi.entities

import javax.persistence.*

@Entity
class Student (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = Int.MIN_VALUE,

    @Column(nullable=false,unique=true)
    var studentId: String,

    var firstName: String,
    var lastName: String
)