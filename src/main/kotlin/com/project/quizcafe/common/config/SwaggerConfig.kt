package com.project.quizcafe.config

import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun customOpenAPI(): io.swagger.v3.oas.models.OpenAPI {
        return io.swagger.v3.oas.models.OpenAPI()
            .info(
                Info()
                    .title("QuizCafe API")
                    .version("1.0.0")
                    .description("QuizCafe API 문서입니다.")
            )
    }
}
