package com.project.quizcafe.user.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class MailServiceImpl(
    private val mailSender: JavaMailSender
) : MailService {
    private val verificationCodes: MutableMap<String, String> = mutableMapOf()
    @Value("\${spring.mail.username}")
    private lateinit var senderEmail: String

    override fun sendMail(toMail:String){
        val mimeMessage = mailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage, false, null)
        val senderName = "QuizCafe"
        mimeMessageHelper.setFrom(senderEmail, senderName)
        mimeMessageHelper.setTo(toMail)
        mimeMessageHelper.setSubject("인증번호")
        val code = Random.nextInt(100000,1000000).toString()
        verificationCodes[toMail] = code
        mimeMessageHelper.setText(code)
        mailSender.send(mimeMessage)
    }

    // 인증 코드 검증
    override fun verifyCode(toMail: String, code: String): Boolean {
        val storedCode = verificationCodes[toMail]
        return storedCode == code  // 비교 후 결과 반환
    }

}