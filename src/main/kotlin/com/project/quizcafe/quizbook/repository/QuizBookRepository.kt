package com.project.quizcafe.quizbook.repository

import com.project.quizcafe.quizbook.entity.QuizBook
import org.springframework.data.jpa.repository.JpaRepository

interface QuizBookRepository : JpaRepository<QuizBook, Long> {
    // 예: 카테고리별 문제집 찾기
    fun findByCategory(category: String): List<QuizBook>
}