package com.project.quizcafe.domain.quiz.repository

import com.project.quizcafe.domain.quiz.entity.Quiz
import org.springframework.data.jpa.repository.JpaRepository

interface QuizRepository : JpaRepository<Quiz, Long>{
    fun findAllByQuizBookId(quizBookId: Long): List<Quiz>
}