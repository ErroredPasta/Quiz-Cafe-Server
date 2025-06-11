package com.project.quizcafe.domain.quizbooksolving.validator

import com.project.quizcafe.common.exception.*
import com.project.quizcafe.domain.quizbooksolving.entity.QuizBookSolving
import com.project.quizcafe.domain.quizbooksolving.repository.QuizBookSolvingRepository
import com.project.quizcafe.domain.user.entity.User
import org.springframework.stereotype.Component

@Component
class QuizBookSolvingValidator (
    private val quizBookSolvingRepository: QuizBookSolvingRepository
) {
    fun validateQuizBookSolvingNotExist(id: Long): QuizBookSolving {
        val quizBookSolving = quizBookSolvingRepository.findById(id)
            .orElseThrow { NotFoundException("해당 ID의 문제 풀이가 존재하지 않습니다: $id") }
        return quizBookSolving
    }

    fun validateMyQuizBookSolving(quizBookSolving: QuizBookSolving, user: User) {
        if (quizBookSolving.user.id != user.id) {
            throw ForbiddenException("조회 권한이 없습니다.")
        }
    }
}