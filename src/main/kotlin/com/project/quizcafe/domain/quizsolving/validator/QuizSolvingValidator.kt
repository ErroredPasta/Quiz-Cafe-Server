package com.project.quizcafe.domain.quizsolving.validator

import com.project.quizcafe.common.exception.*
import com.project.quizcafe.domain.quizsolving.entity.QuizSolving
import com.project.quizcafe.domain.user.entity.User
import org.springframework.stereotype.Component

@Component
class QuizSolvingValidator {
    fun validateMyQuizSolving(quizSolving: QuizSolving, user: User) {
        if (quizSolving.user.id != user.id) {
            throw ForbiddenException("조회 권한이 없습니다.")
        }
    }
}