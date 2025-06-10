package com.project.quizcafe.domain.auth.validator

import com.project.quizcafe.common.exception.AuthenticationException
import com.project.quizcafe.domain.auth.dto.request.SignInRequest
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class SignInValidator(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun validate(request: SignInRequest): User {
        val user = validateEmail(request.loginEmail)
        validatePassword(request.password, user.password)
        return user
    }

    private fun validateEmail(email: String): User {
        return userRepository.findByLoginEmail(email)
            ?: throw AuthenticationException("존재하지 않는 이메일입니다.")
    }

    private fun validatePassword(rawPassword: String, encodedPassword: String) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw AuthenticationException("비밀번호가 일치하지 않습니다.")
        }
    }
}
