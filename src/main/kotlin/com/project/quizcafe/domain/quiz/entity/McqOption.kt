package com.project.quizcafe.domain.quiz.entity

import jakarta.persistence.*

@Entity
@Table(name = "mcq_option")
class McqOption(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    val quiz: Quiz,

    @Column(nullable = false)
    var optionNumber: Int,

    @Column(nullable = false)
    var optionContent: String,

    @Column(nullable = false)
    var isCorrect: Boolean
)
