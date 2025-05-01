package com.project.quizcafe.auth.service

import com.project.quizcafe.auth.dto.request.SendCodeRequest
import com.project.quizcafe.auth.dto.request.SignInRequest
import com.project.quizcafe.auth.dto.request.SignUpRequest
import com.project.quizcafe.auth.dto.response.TokenResponse
import com.project.quizcafe.auth.entity.VerificationType

interface AuthService {
    fun signUp(request: SignUpRequest)
    fun signIn(request: SignInRequest): TokenResponse
    fun sendCode(toMail: String, type: VerificationType)
    fun verifyCode(toMail: String, code: String): Boolean
    fun resetPassword(email: String): Boolean
}