package com.project.quizcafe.domain.quizsolving.entity

import jakarta.persistence.*

@Entity
@Table(name = "mcq_option_solving")
data class McqOptionSolving(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "quiz_solving_id", nullable = false)
    val quizSolving: QuizSolving,

    @Column(name = "option_number", nullable = false)
    var optionNumber: Int,

    @Column(name = "option_content", nullable = false)
    var optionContent: String,

    @Column(name = "is_correct", nullable = false)
    var isCorrect: Boolean = false
)