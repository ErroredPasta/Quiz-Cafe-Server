package com.project.quizcafe.domain.versioncontrol.entity

import jakarta.persistence.*

@Entity
@Table(name = "version")
data class Vc(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "quiz_book_id", nullable = false)
    val quizBookId: Long,

    @Column(nullable = false)
    val version: Long,

    @Column(name = "value", columnDefinition = "TEXT")
    val quizzesValue: String
)