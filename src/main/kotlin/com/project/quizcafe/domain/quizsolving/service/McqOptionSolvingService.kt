package com.project.quizcafe.domain.quizsolving.service

import com.project.quizcafe.domain.quizsolving.dto.request.CreateMcqOptionSolvingRequest
import com.project.quizcafe.domain.quizsolving.dto.request.CreateSingleMcqOptionSolvingRequest
import com.project.quizcafe.domain.quizsolving.dto.request.UpdateMcqOptionSolvingRequest
import com.project.quizcafe.domain.quizsolving.dto.response.McqOptionSolvingResponse
import com.project.quizcafe.domain.quizsolving.entity.McqOptionSolving
import com.project.quizcafe.domain.quizsolving.repository.McqOptionSolvingRepository
import com.project.quizcafe.domain.quizsolving.repository.QuizSolvingRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.*

@Service
@Transactional
class McqOptionSolvingService(
    private val mcqOptionSolvingRepository: McqOptionSolvingRepository,
    private val quizSolvingRepository: QuizSolvingRepository
) {

    fun createMcqOptionSolving(request: CreateSingleMcqOptionSolvingRequest): McqOptionSolving {
        val quizSolving = quizSolvingRepository.findById(request.quizSolvingId)
            .orElseThrow { RuntimeException("퀴즈 풀이를 찾을 수 없습니다. id=${request.quizSolvingId}") }

        val mcqOptionSolving = McqOptionSolving(
            quizSolving = quizSolving,
            optionNumber = request.optionNumber,
            optionContent = request.optionContent,
            isCorrect = request.isCorrect
        )

        return mcqOptionSolvingRepository.save(mcqOptionSolving)
    }

    fun updateMcqOptionSolving(id: Long, request: UpdateMcqOptionSolvingRequest) {
        val mcqOptionSolving = mcqOptionSolvingRepository.findById(id)
            .orElseThrow { RuntimeException("옵션 풀이를 찾을 수 없습니다. id=$id") }

        request.optionNumber?.let { mcqOptionSolving.optionNumber = it }
        request.optionContent?.let { mcqOptionSolving.optionContent = it }
        request.isCorrect?.let { mcqOptionSolving.isCorrect = it }

        mcqOptionSolvingRepository.save(mcqOptionSolving)
    }

    fun deleteMcqOptionSolving(id: Long) {
        val mcqOptionSolving = mcqOptionSolvingRepository.findById(id)
            .orElseThrow { RuntimeException("옵션 풀이를 찾을 수 없습니다. id=$id") }

        mcqOptionSolvingRepository.delete(mcqOptionSolving)
    }

    fun getMcqOptionsByQuizSolvingId(quizSolvingId: Long): List<McqOptionSolvingResponse> {
        val mcqOptions = mcqOptionSolvingRepository.findByQuizSolvingId(quizSolvingId)
        return mcqOptions.map {
            McqOptionSolvingResponse(
                id = it.id,
                quizSolvingId = it.quizSolving.id,
                optionNumber = it.optionNumber,
                optionContent = it.optionContent,
                isCorrect = it.isCorrect
            )
        }
    }
}