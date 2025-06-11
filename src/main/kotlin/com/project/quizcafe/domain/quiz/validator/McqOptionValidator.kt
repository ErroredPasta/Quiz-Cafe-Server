package com.project.quizcafe.domain.quiz.validator

import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.quiz.entity.McqOption
import com.project.quizcafe.domain.quiz.repository.McqOptionRepository
import org.springframework.stereotype.Component

@Component
class McqOptionValidator (
    private val mcqOptionRepository: McqOptionRepository
) {
    fun validateMcqOptionNotExist(id: Long): McqOption {
        val mcqOption = mcqOptionRepository.findById(id)
            .orElseThrow { NotFoundException("해당 ID의 객관식 보기가 존재하지 않습니다: $id") }
        return mcqOption
    }
}