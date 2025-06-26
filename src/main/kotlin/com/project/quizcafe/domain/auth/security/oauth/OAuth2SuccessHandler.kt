package com.project.quizcafe.domain.auth.security.oauth

import com.project.quizcafe.common.model.Role
import com.project.quizcafe.domain.auth.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.util.*

@Component
class OAuth2SuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuthUser = authentication.principal as OAuth2User
        val email = oAuthUser.getAttribute<String>("email") ?: return
        val sessionId = UUID.randomUUID().toString()
        val token = jwtTokenProvider.generateToken(email, Role.USER, sessionId)

        // JWT 응답
        response.contentType = "application/json"
        response.characterEncoding = "utf-8"
        response.writer.write("{\"token\":\"$token\"}")
    }
}
