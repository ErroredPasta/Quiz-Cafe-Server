package com.project.quizcafe.domain.quizsolving.entity

import com.project.quizcafe.domain.quiz.entity.QuestionType
import com.project.quizcafe.domain.quiz.entity.Quiz
import com.project.quizcafe.domain.quizbooksolving.entity.QuizBookSolving
import com.project.quizcafe.domain.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "quiz_solving")
data class QuizSolving(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_book_solving_id")
    val quizBookSolving: QuizBookSolving,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    val quiz: Quiz,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(nullable = true)
    var memo: String?,

    @Column(name = "user_answer", nullable = true)
    var userAnswer: String?,

    @Column(name = "is_correct", nullable = false)
    var isCorrect: Boolean = false,

    @Column(name = "completed_at", nullable = false)
    val completedAt: LocalDateTime = LocalDateTime.now()
)
