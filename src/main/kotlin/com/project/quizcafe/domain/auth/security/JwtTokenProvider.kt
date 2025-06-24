package com.project.quizcafe.domain.auth.security

import com.project.quizcafe.common.model.Role
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${spring.jwt.secret}") secretKey: String,
    @Value("\${spring.jwt.expiration}") private val accessTokenExpiration: Long,
    @Value("\${spring.jwt.refresh-expiration}") private val refreshTokenExpiration: Long
){
    private val key: SecretKey = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun generateToken(email: String, role: Role): String {
        val claims = Jwts.claims().setSubject(email)
        claims["role"] = role.name
        val now = Date()
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + accessTokenExpiration))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun generateRefreshToken(email: String, role: Role): String {
        val claims = Jwts.claims().setSubject(email)
        claims["role"] = role.name
        val now = Date()
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + refreshTokenExpiration))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val email = getEmail(token)
        val role = getRole(token)
        val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))
        return UsernamePasswordAuthenticationToken(email, "", authorities)
    }

    fun getEmail(token: String): String =
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body.subject

    fun getRole(token: String): String =
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body["role"].toString()

    fun validateToken(token: String): Boolean =
        try {
            val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
}

