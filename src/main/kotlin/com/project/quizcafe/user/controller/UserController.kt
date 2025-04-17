package com.project.quizcafe.user.controller

import com.project.quizcafe.user.dto.request.SendMailRequest
import com.project.quizcafe.user.dto.request.VerifyCodeRequest
import com.project.quizcafe.user.service.MailService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
    private val mailService: MailService
) {

    @GetMapping("/test")
    fun test(): ResponseEntity<String> {
        return ResponseEntity.ok("qwerqwer")
    }

    @PostMapping("/send-mail")
    fun sendMail(@RequestBody request: SendMailRequest): ResponseEntity<Void> {
        try {
            mailService.sendMail(request.toMail)
            return ResponseEntity.noContent().build()
        } catch (e: Exception) {
            print(e.toString())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PostMapping("/verify-code")
    fun verifyCode(@RequestBody request: VerifyCodeRequest): ResponseEntity<String> {
        val isValid = mailService.verifyCode(request.toMail, request.code)
        return if (isValid) {
            ResponseEntity.ok("인증 성공")
        } else {
            ResponseEntity.status(400).body("인증 실패")
        }
    }

}