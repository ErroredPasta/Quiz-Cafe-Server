package com.project.quizcafe

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
class QuizcafeApplication

fun main(args: Array<String>) {
	runApplication<QuizcafeApplication>(*args)
}
