package com.project.quizcafe.domain.quizbooksolving.entity

import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.quizbook.entity.QuizLevel
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "quiz_book_solving")
class QuizBookSolving(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_book_id", nullable = false)
    var quizBook: QuizBook,

    @Column(nullable = false)
    var version: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var level: QuizLevel,

    @Column(nullable = false)
    var category: String,

    @Column(nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false)
    var totalQuizzes: Int,

    @Column(nullable = false)
    var correctCount: Int,

    @Column(name = "completed_at", nullable = false)
    var completedAt: LocalDateTime = LocalDateTime.now()
)