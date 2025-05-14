package com.project.quizcafe.domain.quizsolving.repository

import com.project.quizcafe.domain.quizsolving.entity.McqOptionSolving
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface McqOptionSolvingRepository : JpaRepository<McqOptionSolving, Long> {
    fun findByQuizSolvingId(quizSolvingId: Long): List<McqOptionSolving>
}