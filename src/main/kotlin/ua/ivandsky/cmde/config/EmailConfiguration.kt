package ua.ivandsky.cmde.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class EmailConfiguration(
    @Value("\${spring.mail.username}")
    val emailUsername: String,
    @Value("\${spring.mail.password}")
    val emailPassword: String,
) {
    @Bean
    fun mailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()

        mailSender.apply {
            host = "smtp.gmail.com"
            port = 587
            username = emailUsername
            password = emailPassword
        }

        val props = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.debug"] = "true"

        return mailSender
    }

}