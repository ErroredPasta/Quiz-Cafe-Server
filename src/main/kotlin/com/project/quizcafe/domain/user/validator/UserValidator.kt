package com.project.quizcafe.domain.user.validator

import com.project.quizcafe.common.exception.AuthenticationException
import com.project.quizcafe.domain.user.entity.User
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

    fun validateOauthUser(user: User) {
        if(user.provider != User.Provider.LOCAL){
            throw AuthenticationException("OAuth 계정은 일반 로그인으로 이용할 수 없습니다.")
        }
    }
}
