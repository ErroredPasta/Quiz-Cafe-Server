package com.project.quizcafe.domain.quizsolving.repository

import com.project.quizcafe.domain.quizsolving.entity.QuizSolving
import org.springframework.data.jpa.repository.JpaRepository

interface QuizSolvingRepository : JpaRepository<QuizSolving, Long> {
    fun findByQuizBookSolvingId(quizBookSolvingId: Long): List<QuizSolving>
}
