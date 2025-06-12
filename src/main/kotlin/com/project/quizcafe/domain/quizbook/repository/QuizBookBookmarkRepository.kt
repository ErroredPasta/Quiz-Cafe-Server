package com.project.quizcafe.domain.quizbook.repository

import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quizbook.entity.QuizBookBookmark
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizBookBookmarkRepository : JpaRepository<QuizBookBookmark, Long> {
    fun findByUserIdAndQuizBookId(userId: Long, quizBookId: Long): QuizBookBookmark?
    fun findAllByUserId(userId: Long): List<QuizBookBookmark>
    fun deleteByUserIdAndQuizBookId(userId: Long, quizBookId: Long)
    fun findAllByQuizBookId(quizBookId: Long): List<QuizBookBookmark>
    fun existsByUserIdAndQuizBookId(userId: Long, quizBookId: Long): Boolean
}

fun QuizBookBookmarkRepository.getQuizBookBookmarkByUserIdAndQuizBookId(
    userId: Long,
    quizBookId: Long
): QuizBookBookmark = findByUserIdAndQuizBookId(userId, quizBookId) ?: throw NotFoundException("북마크가 존재하지 않습니다.")
