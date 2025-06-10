package com.project.quizcafe.domain.auth.validator

import com.project.quizcafe.common.exception.*
import com.project.quizcafe.domain.auth.dto.request.SignUpRequest
import com.project.quizcafe.domain.user.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class SignUpValidator(
    private val userRepository: UserRepository
) {
    fun validate(request: SignUpRequest) {
        validateEmailDuplicate(request.loginEmail)
        validateNicknameDuplicate(request.nickName)
    }

    private fun validateEmailDuplicate(email: String) {
        if (userRepository.existsByLoginEmail(email)) {
            throw ConflictException("이미 존재하는 이메일입니다.")
        }
    }

    private fun validateNicknameDuplicate(nickName: String) {
        if (userRepository.existsByNickName(nickName)) {
            throw ConflictException("이미 사용 중인 닉네임입니다.")
        }
    }
}
