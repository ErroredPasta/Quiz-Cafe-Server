package com.project.quizcafe.domain.quizbook.validator

import com.project.quizcafe.common.exception.*
import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.user.entity.User
import org.springframework.stereotype.Component

@Component
class QuizBookValidator {
    fun validateMyQuizBook(quizBook: QuizBook, user: User) {
        quizBook.createdBy?.let {
            if (it.id != user.id) {
                throw ForbiddenException("문제집을 수정할 권한이 없습니다.")
            }
        }
    }
}