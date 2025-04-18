package com.project.quizcafe.auth.service

import com.project.quizcafe.auth.dto.request.LoginRequest
import com.project.quizcafe.auth.dto.request.SignupRequest
import com.project.quizcafe.auth.dto.response.TokenResponse

interface AuthService {
    fun signup(request: SignupRequest)
    fun login(request: LoginRequest): TokenResponse
    fun sendMail(toMail:String)
    fun verifyCode(toMail: String, code: String): Boolean
    fun resetPassword(email: String): Boolean
}