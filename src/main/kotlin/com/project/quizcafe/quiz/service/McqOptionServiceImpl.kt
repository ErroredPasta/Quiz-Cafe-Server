package com.project.quizcafe.quiz.service

import com.project.quizcafe.auth.security.UserDetailsImpl
import com.project.quizcafe.quiz.dto.request.CreateMcqOptionRequest
import com.project.quizcafe.quiz.dto.request.UpdateMcqOptionRequest
import com.project.quizcafe.quiz.dto.response.McqOptionResponse
import com.project.quizcafe.quiz.entity.McqOption
import com.project.quizcafe.quiz.repository.McqOptionRepository
import com.project.quizcafe.quiz.repository.QuizRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class McqOptionServiceImpl(
    private val mcqOptionRepository: McqOptionRepository,
    private val quizRepository: QuizRepository
) : McqOptionService {

    override fun createMcqOption(request: CreateMcqOptionRequest): McqOptionResponse {
        val quiz = quizRepository.findById(request.quizId)
            .orElseThrow { IllegalArgumentException("퀴즈를 찾을 수 없습니다.") }
        val currentUser = (SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl).getUser()

        val quizBook = quiz.quizBook
        val quizBookCreator = quizBook.createdBy
        if (quizBookCreator?.id != currentUser.id) {
            throw IllegalArgumentException("퀴즈북의 생성자만 수정할 수 있습니다.")
        }

        val mcqOption = McqOption(
            quiz = quiz,
            optionNumber = request.optionNumber,
            optionContent = request.optionContent,
            isCorrect = request.isCorrect
        )

        val savedMcqOption = mcqOptionRepository.save(mcqOption)

        return McqOptionResponse(
            id = savedMcqOption.id,
            quizId = savedMcqOption.quiz.id,
            optionNumber = savedMcqOption.optionNumber,
            optionContent = savedMcqOption.optionContent,
            isCorrect = savedMcqOption.isCorrect
        )
    }

    @Transactional
    override fun updateMcqOption(id: Long, request: UpdateMcqOptionRequest) {
        // 요청한 사용자의 정보 가져오기
        val currentUser = (SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl).getUser()

        // MCQ 옵션 조회
        val mcqOption = mcqOptionRepository.findById(id)
            .orElseThrow { Exception("객관식 보기 옵션을 찾을 수 없습니다.") }

        // 퀴즈북 조회 (퀴즈가 포함된 퀴즈북의 생성자 정보 확인)
        val quiz = mcqOption.quiz
        val quizBook = quiz.quizBook
        val quizBookCreator = quizBook.createdBy

        // 퀴즈북 생성자와 현재 사용자가 일치하는지 확인
        if (quizBookCreator?.id != currentUser.id) {
            throw IllegalArgumentException("퀴즈북의 생성자만 수정할 수 있습니다.")
        }

        // 수정할 수 있는 값만 수정
        request.optionContent?.let { mcqOption.optionContent = it }
        request.isCorrect?.let { mcqOption.isCorrect = it }

        mcqOptionRepository.save(mcqOption) // 변경된 내용 저장

        mcqOption.quiz.version++
        mcqOption.quiz.quizBook.version++
    }


    override fun deleteMcqOption(id: Long) {
        // 요청한 사용자의 정보 가져오기
        val currentUser = (SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl).getUser()

        // MCQ 옵션 조회
        val mcqOption = mcqOptionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("객관식 보기 옵션을 찾을 수 없습니다.") }

        // 퀴즈북 조회 (퀴즈가 포함된 퀴즈북의 생성자 정보 확인)
        val quiz = mcqOption.quiz
        val quizBook = quiz.quizBook
        val quizBookCreator = quizBook.createdBy

        // 퀴즈북 생성자와 현재 사용자가 일치하는지 확인
        if (quizBookCreator?.id != currentUser.id) {
            throw IllegalArgumentException("퀴즈북의 생성자만 삭제할 수 있습니다.")
        }

        mcqOptionRepository.delete(mcqOption) // 삭제
    }

    override fun getMcqOptionsByQuizId(quizId: Long): List<McqOptionResponse> {
        val mcqOptions = mcqOptionRepository.findByQuizId(quizId)
        return mcqOptions.map { mcqOption ->
            McqOptionResponse(
                id = mcqOption.id,
                quizId = mcqOption.quiz.id,
                optionNumber = mcqOption.optionNumber,
                optionContent = mcqOption.optionContent,
                isCorrect = mcqOption.isCorrect
            )
        }
    }
}
