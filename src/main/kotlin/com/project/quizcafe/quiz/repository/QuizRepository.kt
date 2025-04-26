package com.project.quizcafe.quiz.repository

import com.project.quizcafe.quiz.entity.Quiz
import org.springframework.data.jpa.repository.JpaRepository

interface QuizRepository : JpaRepository<Quiz, Long>{
    fun findAllByQuizBookId(quizBookId: Long): List<Quiz>
}