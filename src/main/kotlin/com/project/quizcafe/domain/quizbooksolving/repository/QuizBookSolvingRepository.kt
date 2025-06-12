package com.project.quizcafe.domain.quizbooksolving.repository

import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quizbooksolving.entity.QuizBookSolving
import com.project.quizcafe.domain.quizsolving.entity.QuizSolving
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface QuizBookSolvingRepository : JpaRepository<QuizBookSolving, Long> {
    fun findByUserId(userId: Long): List<QuizBookSolving>
    fun findByQuizBookId(quizBookId: Long): List<QuizBookSolving>
    @Query("SELECT AVG(qs.correctCount) FROM QuizBookSolving qs WHERE qs.quizBook.id = :quizBookId")
    fun findAvgCorrectCountByQuizBookId(@Param("quizBookId") quizBookId: Long): Double?
}

fun QuizBookSolvingRepository.getQuizBookSolvingById(id: Long): QuizBookSolving = findById(id).orElseThrow {
    NotFoundException("해당 ID의 문제 풀이가 존재하지 않습니다: $id")
}
