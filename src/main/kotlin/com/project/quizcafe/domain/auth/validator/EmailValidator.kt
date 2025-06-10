package com.project.quizcafe.domain.auth.validator

import com.project.quizcafe.common.exception.*
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.user.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class EmailValidator(
    private val userRepository: UserRepository
) {

    fun validateEmailExist(email: String) {
        if (userRepository.existsByLoginEmail(email)) {
            throw ConflictException("이미 존재하는 이메일입니다.")
        }
    }

    fun validateEmailNotExist(email: String): User {
        return userRepository.findByLoginEmail(email)
            ?: throw NotFoundException("존재하지 않는 이메일입니다.")
    }
}
