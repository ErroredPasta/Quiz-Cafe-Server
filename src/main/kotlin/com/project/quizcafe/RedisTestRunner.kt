package com.project.quizcafe

import org.springframework.boot.CommandLineRunner
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisTestRunner(
    private val redisTemplate: RedisTemplate<String, String>
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        try {
            redisTemplate.opsForValue().set("testKey", "testValue")
            val value = redisTemplate.opsForValue().get("testKey")
            println("Redis testKey value: $value")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}