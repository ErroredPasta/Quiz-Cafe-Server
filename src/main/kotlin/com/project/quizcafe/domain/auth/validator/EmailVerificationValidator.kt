package com.project.quizcafe.domain.auth.validator

import com.project.quizcafe.common.exception.*
import com.project.quizcafe.domain.auth.entity.EmailVerification
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class EmailVerificationValidator {

    fun validate(emailVerification: EmailVerification?, code: String) {
        requireEmailVerificationExists(emailVerification)
        requireCodeMatches(emailVerification!!, code)
        requireCodeNotExpired(emailVerification)
    }

    private fun requireEmailVerificationExists(emailVerification: EmailVerification?) {
        if (emailVerification == null) {
            throw NotFoundException("등록된 인증 코드가 없습니다.")
        }
    }

    private fun requireCodeMatches(emailVerification: EmailVerification, code: String) {
        if (emailVerification.verificationCode != code) {
            throw BadRequestException("인증 코드가 일치하지 않습니다.")
        }
    }

    private fun requireCodeNotExpired(emailVerification: EmailVerification) {
        if (emailVerification.expiresAt.isBefore(LocalDateTime.now())) {
            throw BadRequestException("인증 코드가 만료되었습니다.")
        }
    }
}
