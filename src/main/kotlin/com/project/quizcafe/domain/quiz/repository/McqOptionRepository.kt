package com.project.quizcafe.domain.quiz.repository

import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quiz.entity.McqOption
import org.springframework.data.jpa.repository.JpaRepository

interface McqOptionRepository : JpaRepository<McqOption, Long> {
    fun findByQuizId(quizId: Long): List<McqOption> // 퀴즈 ID에 대한 선택지 조회
    fun findByIdAndQuizId(id: Long, quizId: Long): McqOption? // 퀴즈 ID와 옵션 ID로 조회
}

fun McqOptionRepository.getMcqOptionById(id: Long): McqOption = findById(id).orElseThrow {
    NotFoundException("해당 ID의 객관식 보기가 존재하지 않습니다: $id")
}
