package com.project.quizcafe.domain.quizbook.service

import com.project.quizcafe.domain.quizbook.entity.QuizBookBookmark
import com.project.quizcafe.domain.quizbook.repository.QuizBookBookmarkRepository
import com.project.quizcafe.domain.quizbook.validator.QuizBookMarkValidator
import com.project.quizcafe.domain.quizbook.validator.QuizBookValidator
import com.project.quizcafe.domain.user.entity.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuizBookBookmarkService(
    private val quizBookBookmarkRepository: QuizBookBookmarkRepository,
    private val quizBookValidator: QuizBookValidator,
    private val quizBookMarkValidator: QuizBookMarkValidator
) {

    @Transactional
    fun addBookmark(user: User, quizBookId: Long) {
        quizBookMarkValidator.validateQuizBookMarkExist(user, quizBookId)
        val quizBook = quizBookValidator.validateQuizBookNotExist(quizBookId)

        val bookmark = QuizBookBookmark(user = user, quizBook = quizBook)
        quizBookBookmarkRepository.save(bookmark)
    }

    @Transactional
    fun removeBookmark(userId: Long, quizBookId: Long) {
        val existingBookmark = quizBookMarkValidator.validateQuizBookMarkNotExist(userId, quizBookId)
        quizBookBookmarkRepository.delete(existingBookmark)
    }

    fun getBookmarksByUserId(user: User): List<Long> {
        return quizBookBookmarkRepository.findAllByUserId(user.id).map { it.quizBook.id }
    }
}
