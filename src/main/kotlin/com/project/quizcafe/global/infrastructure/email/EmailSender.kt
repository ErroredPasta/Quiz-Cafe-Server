package com.project.quizcafe.global.infrastructure.email

import com.project.quizcafe.common.exception.BadRequestException
import com.project.quizcafe.common.exception.InternalServerErrorException
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.io.UnsupportedEncodingException

@Component
class EmailSender(
    private val mailSender: JavaMailSender,
    @Value("\${spring.mail.username}") private val senderEmail: String
) {
    fun sendVerificationCode(email: String, code: String) {
        try {
            val mimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage, false, "UTF-8")

            try {
                helper.setFrom(senderEmail, "QuizCafe")
            } catch (e: UnsupportedEncodingException) {
                throw InternalServerErrorException("발신자 이름 인코딩 중 오류가 발생했습니다.")
            }

            helper.setTo(email)
            helper.setSubject("QuizCafe 이메일 인증번호")
            helper.setText("인증번호: $code")

            mailSender.send(mimeMessage)

        } catch (e: MailException) {
            val msg = e.message ?: ""
            if (msg.contains("550") || msg.contains("Invalid Addresses") || msg.contains("Recipient address rejected")) {
                throw BadRequestException("존재하지 않는 이메일 주소입니다.")
            }
            throw InternalServerErrorException("이메일 전송 중 오류가 발생했습니다.")
        } catch (e: Exception) {
            throw InternalServerErrorException("이메일 생성 중 오류가 발생했습니다.")
        }
    }

    fun sendPasswordResetEmail(to: String, newPassword: String) {
        val mimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, false, "UTF-8")
        val senderName = "QuizCafe"

        try {
            helper.setFrom(senderEmail, senderName)
            helper.setTo(to)
            helper.setSubject("비밀번호 재설정")
            helper.setText("임시 비밀번호: $newPassword")
            mailSender.send(mimeMessage)
        } catch (e: UnsupportedEncodingException) {
            throw InternalServerErrorException("발신자 이름 인코딩 오류")
        } catch (e: MailException) {
            throw InternalServerErrorException("이메일 전송 실패")
        }
    }
}
