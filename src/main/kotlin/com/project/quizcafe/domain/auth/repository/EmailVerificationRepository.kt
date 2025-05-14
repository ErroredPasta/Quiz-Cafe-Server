package com.project.quizcafe.domain.auth.repository

import com.project.quizcafe.domain.auth.entity.EmailVerification
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface EmailVerificationRepository : JpaRepository<EmailVerification, Long> {
    fun findByEmail(email: String): Optional<EmailVerification>
    @Transactional
    fun deleteByEmail(email: String): Int  // 이메일로 삭제

    @Modifying
    @Transactional
    @Query("DELETE FROM EmailVerification e WHERE e.expiresAt < :now")
    fun deleteExpiredVerificationCodes(now: LocalDateTime): Int
}
