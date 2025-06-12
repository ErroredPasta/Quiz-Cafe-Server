package com.project.quizcafe.domain.quiz.repository

import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quiz.entity.Quiz
import org.springframework.data.jpa.repository.JpaRepository

interface QuizRepository : JpaRepository<Quiz, Long>{
    fun findAllByQuizBookId(quizBookId: Long): List<Quiz>
}

fun QuizRepository.getByQuizBookId(id: Long): Quiz = findById(id).orElseThrow {
    NotFoundException("해당 ID의 퀴즈가 존재하지 않습니다: $id")
}