package com.project.quizcafe.auth.service

import com.project.quizcafe.auth.repository.EmailVerificationRepository
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EmailVerificationService(
    private val emailVerificationRepository: EmailVerificationRepository
) {

    // 매 1분마다 만료된 인증 코드를 삭제
    @Scheduled(cron = "0 * * * * ?")
    //@Scheduled(cron = "*/5 * * * * *")
    public fun deleteExpiredVerificationCodes() {
        val now = LocalDateTime.now()
        val deletedCount = emailVerificationRepository.deleteExpiredVerificationCodes(now)
        println("Deleted $deletedCount expired verification codes.")
    }
}
