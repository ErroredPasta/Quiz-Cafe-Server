package com.project.quizcafe.quizbook.service

import com.project.quizcafe.quizbook.entity.QuizBookBookmark
import com.project.quizcafe.quizbook.repository.QuizBookBookmarkRepository
import com.project.quizcafe.quizbook.repository.QuizBookRepository
import com.project.quizcafe.user.entity.User
import com.project.quizcafe.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuizBookBookmarkService(
    private val quizBookBookmarkRepository: QuizBookBookmarkRepository,
    private val userRepository: UserRepository,
    private val quizBookRepository: QuizBookRepository
) {

    // 북마크 추가
    @Transactional
    fun addBookmark(userId: Long, quizBookId: Long): QuizBookBookmark {
        // 이미 존재하는 북마크는 중복으로 추가하지 않도록
        val existingBookmark = quizBookBookmarkRepository.findByUserIdAndQuizBookId(userId, quizBookId)
        if (existingBookmark != null) {
            throw IllegalStateException("이미 북마크한 퀴즈북입니다.")
        }

        val user = userRepository.findById(userId).orElseThrow { IllegalStateException("사용자를 찾을 수 없습니다.") }
        val quizBook = quizBookRepository.findById(quizBookId).orElseThrow { IllegalStateException("퀴즈북을 찾을 수 없습니다.") }

        val bookmark = QuizBookBookmark(user = user, quizBook = quizBook)
        return quizBookBookmarkRepository.save(
            bookmark
        )
    }

    // 북마크 삭제
    @Transactional
    fun removeBookmark(userId: Long, quizBookId: Long) {
        val existingBookmark = quizBookBookmarkRepository.findByUserIdAndQuizBookId(userId, quizBookId)
        if (existingBookmark == null) {
            throw IllegalStateException("북마크가 존재하지 않습니다.")
        }

        quizBookBookmarkRepository.delete(existingBookmark)
    }

    // 특정 사용자의 북마크 목록 조회
    fun getBookmarksByUserId(user: User): List<Long> {
        val userId = user.id
        if(userId==null){
            throw Exception()
        }
        return quizBookBookmarkRepository.findAllByUserId(userId).map { it.quizBook.id }
    }
}
