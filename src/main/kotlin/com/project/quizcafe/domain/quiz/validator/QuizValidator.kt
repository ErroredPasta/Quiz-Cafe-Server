package com.project.quizcafe.domain.quiz.validator

import com.project.quizcafe.common.exception.*
import com.project.quizcafe.domain.quiz.entity.Quiz
import com.project.quizcafe.domain.quiz.repository.QuizRepository
import com.project.quizcafe.domain.user.entity.User
import org.springframework.stereotype.Component

@Component
class QuizValidator (
    private val quizRepository: QuizRepository
) {
    fun validateQuizNotExist(id: Long): Quiz {
        val quiz = quizRepository.findById(id)
            .orElseThrow { NotFoundException("해당 ID의 퀴즈가 존재하지 않습니다: $id") }
        return quiz
    }

    fun validateMyQuiz(quiz: Quiz, user: User) {
        quiz.quizBook.createdBy?.let {
            if (it.id != user.id) {
                throw ForbiddenException("문제를 수정할 권한이 없습니다.")
            }
        }
    }
}