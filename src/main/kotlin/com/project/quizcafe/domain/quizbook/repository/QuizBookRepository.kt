package com.project.quizcafe.domain.quizbook.repository

import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface QuizBookRepository : JpaRepository<QuizBook, Long> {
    fun findByCategory(category: String): List<QuizBook>
    fun findByCreatedBy(user: User): List<QuizBook>
}