package com.project.quizcafe.domain.quizbooksolving.validator

import com.project.quizcafe.common.exception.*
import com.project.quizcafe.domain.quizbooksolving.entity.QuizBookSolving
import com.project.quizcafe.domain.user.entity.User
import org.springframework.stereotype.Component

@Component
class QuizBookSolvingValidator {
    fun validateMyQuizBookSolving(quizBookSolving: QuizBookSolving, user: User) {
        if (quizBookSolving.user.id != user.id) {
            throw ForbiddenException("조회 권한이 없습니다.")
        }
    }
}