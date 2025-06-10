package com.project.quizcafe.domain.auth.service

import com.project.quizcafe.common.exception.BadRequestException
import com.project.quizcafe.common.exception.InternalServerErrorException
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.auth.entity.EmailVerification
import com.project.quizcafe.domain.auth.repository.EmailVerificationRepository
import com.project.quizcafe.domain.auth.validator.EmailVerificationValidator
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class EmailVerificationService(
    private val emailVerificationRepository: EmailVerificationRepository,
    private val emailVerificationValidator: EmailVerificationValidator
) {

    fun generateAndSaveCode(email: String): String {
        val code = Random.nextInt(100000, 1000000).toString()
        try {
            emailVerificationRepository.deleteByEmail(email)
            emailVerificationRepository.save(
                EmailVerification(email = email, verificationCode = code)
            )
        } catch (e: Exception) {
            throw InternalServerErrorException("인증 코드 저장 중 오류가 발생했습니다.")
        }
        return code
    }

    fun verifyCode(toMail: String, code: String): Boolean {
        val emailVerification = emailVerificationRepository.findByEmail(toMail).orElse(null)
        emailVerificationValidator.validate(emailVerification, code)
        return true
    }

    // 매 1분마다 만료된 인증 코드를 삭제
    @Scheduled(cron = "0 * * * * ?")
    //@Scheduled(cron = "*/5 * * * * *")
    public fun deleteExpiredVerificationCodes() {
        val now = LocalDateTime.now()
        val deletedCount = emailVerificationRepository.deleteExpiredVerificationCodes(now)
        println("Deleted $deletedCount expired verification codes.")
    }
}
