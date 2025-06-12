package com.project.quizcafe.domain.quizbook.repository

import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface QuizBookRepository : JpaRepository<QuizBook, Long> {
    fun findAllByCategory(category: String): List<QuizBook>
    fun findByCreatedBy(user: User): List<QuizBook>
}

fun QuizBookRepository.getQuizBookById(id: Long): QuizBook = findById(id).orElseThrow {
    NotFoundException("해당 ID의 퀴즈북이 존재하지 않습니다: $id")
}
