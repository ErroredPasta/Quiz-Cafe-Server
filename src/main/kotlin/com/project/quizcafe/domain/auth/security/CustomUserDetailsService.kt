package com.project.quizcafe.domain.auth.security

import com.project.quizcafe.domain.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetailsImpl {
        val user = userRepository.findByLoginEmail(email)
            ?: throw UsernameNotFoundException("이메일이 존재하지 않습니다.")
        return UserDetailsImpl(user)
    }

}