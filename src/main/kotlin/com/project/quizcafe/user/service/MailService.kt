package com.project.quizcafe.user.service

interface MailService {
    fun sendMail(toMail:String)
    fun verifyCode(toMail: String, code: String): Boolean
}