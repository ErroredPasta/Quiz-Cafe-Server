package com.project.quizcafe.domain.user.validator

import com.project.quizcafe.common.exception.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserValidator(
    private val passwordEncoder: PasswordEncoder
) {
    fun validatePasswordCorrect(rawPassword: String, encodedPassword: String) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw AuthenticationException("비밀번호가 일치하지 않습니다.")
        }
    }
}
