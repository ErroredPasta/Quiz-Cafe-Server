package com.project.quizcafe.domain.quizbook.validator

import com.project.quizcafe.common.exception.*
import com.project.quizcafe.domain.quizbook.entity.QuizBook
import com.project.quizcafe.domain.quizbook.repository.QuizBookRepository
import com.project.quizcafe.domain.user.entity.User
import org.springframework.stereotype.Component

@Component
class QuizBookValidator(
    private val quizBookRepository: QuizBookRepository
) {

    fun validateQuizBookNotExist(id: Long): QuizBook {
        val quizBook = quizBookRepository.findById(id)
            .orElseThrow { NotFoundException("해당 ID의 퀴즈북이 존재하지 않습니다: $id") }
        return quizBook
    }

    fun validateMyQuizBook(quizBook: QuizBook, user: User) {
        quizBook.createdBy?.let {
            if (it.id != user.id) {
                throw ForbiddenException("문제집을 수정할 권한이 없습니다.")
            }
        }
    }
}