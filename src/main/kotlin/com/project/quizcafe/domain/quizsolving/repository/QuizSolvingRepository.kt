package com.project.quizcafe.domain.quizsolving.repository

import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quizsolving.entity.QuizSolving
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.data.jpa.repository.JpaRepository

interface QuizSolvingRepository : JpaRepository<QuizSolving, Long> {
    fun findByQuizBookSolvingId(quizBookSolvingId: Long): List<QuizSolving>
    fun findByQuizBookSolvingIdAndQuizId(quizBookSolvingId: Long, quizId: Long): QuizSolving?
}

fun QuizSolvingRepository.getQuizSolvingById(id: Long): QuizSolving = findById(id).orElseThrow {
    NotFoundException("해당 ID의 문제 풀이가 존재하지 않습니다: $id")
}
