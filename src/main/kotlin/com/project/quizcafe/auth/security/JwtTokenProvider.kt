package com.project.quizcafe.auth.security

import com.project.quizcafe.common.model.Role
import io.jsonwebtoken.*
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
    @Value("\${spring.jwt.secret}") secretKey: String
){
    private val key: SecretKey = Keys.hmacShaKeyFor(secretKey.toByteArray())
    private val validityInMilliseconds = 3600000L//1시간

    //JWT 발급
    fun generateToken(email: String, role: Role): String {
        val claims = Jwts.claims().setSubject(email) // 🔥 email을 subject로!
        claims["role"] = role.name

        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    // 필터에서 인증 객체 생성
    fun getAuthentication(token: String): Authentication {
        val email = getEmail(token) // 🔥 여기서도 이메일 사용
        val role = getRole(token)
        val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))
        return UsernamePasswordAuthenticationToken(email, "", authorities)
    }

    // email 추출
    fun getEmail(token: String): String =
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body.subject

    // role 추출은 동일
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

