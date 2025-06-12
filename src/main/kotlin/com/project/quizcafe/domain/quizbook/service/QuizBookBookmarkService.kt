package com.project.quizcafe.domain.quizbook.service

import com.project.quizcafe.common.exception.ConflictException
import com.project.quizcafe.domain.quizbook.entity.QuizBookBookmark
import com.project.quizcafe.domain.quizbook.repository.QuizBookBookmarkRepository
import com.project.quizcafe.domain.quizbook.repository.QuizBookRepository
import com.project.quizcafe.domain.quizbook.repository.getQuizBookBookmarkByUserIdAndQuizBookId
import com.project.quizcafe.domain.quizbook.repository.getQuizBookById
import com.project.quizcafe.domain.user.entity.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuizBookBookmarkService(
    private val quizBookBookmarkRepository: QuizBookBookmarkRepository,
    private val quizBookRepository: QuizBookRepository
) {

    @Transactional
    fun addBookmark(user: User, quizBookId: Long) {
        if (quizBookBookmarkRepository.existsByUserIdAndQuizBookId(user.id, quizBookId)) {
            throw ConflictException("이미 북마크한 퀴즈북입니다.")
        }

        val quizBook = quizBookRepository.getQuizBookById(quizBookId)

        val bookmark = QuizBookBookmark(user = user, quizBook = quizBook)
        quizBookBookmarkRepository.save(bookmark)
    }

    @Transactional
    fun removeBookmark(userId: Long, quizBookId: Long) {
        val existingBookmark = quizBookBookmarkRepository.getQuizBookBookmarkByUserIdAndQuizBookId(userId, quizBookId)
        quizBookBookmarkRepository.delete(existingBookmark)
    }

    fun getBookmarksByUserId(user: User): List<Long> {
        return quizBookBookmarkRepository.findAllByUserId(user.id).map { it.quizBook.id }
    }
}
