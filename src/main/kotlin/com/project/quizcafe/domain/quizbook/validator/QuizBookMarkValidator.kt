package com.project.quizcafe.domain.quizbook.validator

import com.project.quizcafe.common.exception.*
import com.project.quizcafe.domain.quizbook.entity.QuizBookBookmark
import com.project.quizcafe.domain.quizbook.repository.QuizBookBookmarkRepository
import com.project.quizcafe.domain.user.entity.User
import org.springframework.stereotype.Component

@Component
class QuizBookMarkValidator(
    private val quizBookBookmarkRepository: QuizBookBookmarkRepository
) {

    fun validateQuizBookMarkExist(user: User, quizBookId: Long) {
        val existingBookmark = quizBookBookmarkRepository.findByUserIdAndQuizBookId(user.id, quizBookId)
        if (existingBookmark != null) {
            throw ConflictException("이미 북마크한 퀴즈북입니다.")
        }
    }

    fun validateQuizBookMarkNotExist(userId: Long, quizBookId: Long): QuizBookBookmark {
        return quizBookBookmarkRepository.findByUserIdAndQuizBookId(userId, quizBookId)
            ?: throw NotFoundException("북마크가 존재하지 않습니다.")
    }

}