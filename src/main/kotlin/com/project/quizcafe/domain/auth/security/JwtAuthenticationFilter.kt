package com.project.quizcafe.domain.auth.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.project.quizcafe.common.response.ApiResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val customUserDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val objectMapper = jacksonObjectMapper()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = resolveToken(request)

            if (token != null) {
                if (!jwtTokenProvider.validateToken(token)) {
                    log.info("유효하지 않은 JWT 토큰입니다.")
                    setErrorResponse(response, HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다.")
                    return
                }

                val email = jwtTokenProvider.getEmail(token)
                val userDetails = customUserDetailsService.loadUserByUsername(email)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }

            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
            log.error("JWT 인증 처리 중 예외 발생", ex)
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT 인증 처리 중 오류가 발생했습니다.")
        }
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    private fun setErrorResponse(
        response: HttpServletResponse,
        status: HttpStatus,
        message: String
    ) {
        response.status = status.value()
        response.contentType = "application/json;charset=UTF-8"

        val apiResponse = ApiResponse<Any?>(
            status = "fail",
            code = status.value(),
            message = message,
            data = null
        )

        val json = objectMapper.writeValueAsString(apiResponse)
        response.writer.write(json)
        response.writer.flush()
        response.writer.close()
    }
}
