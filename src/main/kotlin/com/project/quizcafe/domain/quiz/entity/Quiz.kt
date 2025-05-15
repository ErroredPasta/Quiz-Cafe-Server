package com.project.quizcafe.domain.quiz.entity

import com.project.quizcafe.domain.quizbook.entity.QuizBook
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "quiz")
class Quiz(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_book_id", nullable = false)
    val quizBook: QuizBook,

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    var questionType: QuestionType,

    @Column(name = "content", nullable = false)
    var content: String,

    @Column(name = "answer", nullable = false)
    var answer: String,

    @Column(name = "explanation")
    var explanation: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)