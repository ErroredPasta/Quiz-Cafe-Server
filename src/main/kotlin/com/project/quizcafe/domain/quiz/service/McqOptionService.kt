package com.project.quizcafe.domain.quiz.service

import com.project.quizcafe.domain.quiz.dto.request.CreateMcqOptionRequest
import com.project.quizcafe.domain.quiz.dto.request.UpdateMcqOptionRequest
import com.project.quizcafe.domain.quiz.dto.response.McqOptionResponse

interface McqOptionService {
    fun createMcqOption(request: CreateMcqOptionRequest): McqOptionResponse
    fun updateMcqOption(id: Long, request: UpdateMcqOptionRequest)
    fun deleteMcqOption(id: Long)
    fun getMcqOptionsByQuizId(quizId: Long): List<McqOptionResponse>
    fun getMcqOptionsById(quizId: Long): McqOptionResponse
}