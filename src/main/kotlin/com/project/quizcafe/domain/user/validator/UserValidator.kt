package com.project.quizcafe.domain.user.validator

import com.project.quizcafe.common.exception.AuthenticationException
import com.project.quizcafe.common.exception.ConflictException
import com.project.quizcafe.common.exception.NotFoundException
import com.project.quizcafe.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserValidator (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun validateNickNameExist(nickName: String) {
        if (userRepository.existsByNickName(nickName)) {
            throw ConflictException("이미 존재하는 닉네임입니다.")
        }
    }

    fun validatePasswordCorrect(rawPassword: String, encodedPassword: String) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw AuthenticationException("비밀번호가 일치하지 않습니다.")
        }
    }
}
