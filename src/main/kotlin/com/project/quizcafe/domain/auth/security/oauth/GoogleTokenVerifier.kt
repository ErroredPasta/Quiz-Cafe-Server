package com.project.quizcafe.domain.auth.security.oauth

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GoogleTokenVerifier(
    @Value("\${spring.security.oauth2.client.registration.google.client-id}") private val clientId: String
) {
    private val transport = NetHttpTransport()
    private val jsonFactory = GsonFactory.getDefaultInstance()

    private val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
        .setAudience(listOf(clientId))
        .build()

    fun verify(token: String): GoogleIdToken.Payload? {
        val idToken: GoogleIdToken? = verifier.verify(token)
        return idToken?.payload
    }
}