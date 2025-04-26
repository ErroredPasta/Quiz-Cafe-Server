package com.project.quizcafe.quiz.repository

import com.project.quizcafe.quiz.entity.McqOption
import org.springframework.data.jpa.repository.JpaRepository

interface McqOptionRepository : JpaRepository<McqOption, Long> {
    fun findByQuizId(quizId: Long): List<McqOption> // 퀴즈 ID에 대한 선택지 조회
    fun findByIdAndQuizId(id: Long, quizId: Long): McqOption? // 퀴즈 ID와 옵션 ID로 조회
}