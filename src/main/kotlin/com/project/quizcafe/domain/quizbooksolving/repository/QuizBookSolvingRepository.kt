package com.project.quizcafe.domain.quizbooksolving.repository

import com.project.quizcafe.domain.quizbooksolving.entity.QuizBookSolving
import com.project.quizcafe.domain.quizsolving.entity.QuizSolving
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizBookSolvingRepository : JpaRepository<QuizBookSolving, Long> {
    fun findByUserId(userId: Long): List<QuizBookSolving>
    fun findByQuizBookId(quizBookId: Long): List<QuizBookSolving>

}
