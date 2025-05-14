package com.project.quizcafe.domain.quizbook.entity

import com.project.quizcafe.domain.user.entity.User
import jakarta.persistence.*

@Entity
@Table(name = "quiz_book_bookmark")
class QuizBookBookmark(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_book_id", nullable = false)
    val quizBook: QuizBook,

    @Column(name = "created_at", nullable = false)
    val createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now()
)
