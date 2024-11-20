package ua.ivandsky.cmde.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class EmailService(
    @Autowired private val emailSender: JavaMailSender
) {
    fun sendVerificationEmail(email: String, subject: String, text: String) {
        val message = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)

        helper.apply {
            setTo(email)
            setSubject(subject)
            setText(text, true)
        }

        emailSender.send(message)
    }
}