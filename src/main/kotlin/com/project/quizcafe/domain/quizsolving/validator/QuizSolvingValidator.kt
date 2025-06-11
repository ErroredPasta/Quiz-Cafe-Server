package com.project.quizcafe.domain.quizsolving.validator

import com.project.quizcafe.common.exception.*
import com.project.quizcafe.domain.quizsolving.entity.QuizSolving
import com.project.quizcafe.domain.quizsolving.repository.QuizSolvingRepository
import com.project.quizcafe.domain.user.entity.User
import org.springframework.stereotype.Component

@Component
class QuizSolvingValidator (
    private val quizSolvingRepository: QuizSolvingRepository
) {
    fun validateQuizSolvingNotExist(id: Long): QuizSolving {
        val quizSolving = quizSolvingRepository.findById(id)
            .orElseThrow { NotFoundException("해당 ID의 문제 풀이가 존재하지 않습니다: $id") }
        return quizSolving
    }

    fun validateMyQuizSolving(quizSolving: QuizSolving, user: User) {
        if (quizSolving.user.id != user.id) {
            throw ForbiddenException("조회 권한이 없습니다.")
        }
    }
}