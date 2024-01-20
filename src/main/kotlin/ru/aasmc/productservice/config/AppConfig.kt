package ru.aasmc.productservice.config

import org.hashids.Hashids
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig(
    @Value("\${crypto.salt}")
    private val salt: String,
    @Value("\${crypto.minHashLength}")
    private val minHashLength: Int
) {

    @Bean
    fun hashIds(): Hashids = Hashids(salt, minHashLength)

}