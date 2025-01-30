package ua.ivandsky.cmde.service

import jakarta.mail.MessagingException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ua.ivandsky.cmde.dto.LoginUserDto
import ua.ivandsky.cmde.dto.RegisterUserDto
import ua.ivandsky.cmde.dto.VerifyUserDto
import ua.ivandsky.cmde.model.User
import ua.ivandsky.cmde.repository.RoleRepository
import ua.ivandsky.cmde.repository.UserRepository
import java.time.LocalDateTime
import java.time.temporal.TemporalAmount

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val emailService: EmailService,
) {
    fun signUp(input: RegisterUserDto): User {
        val user = User(
            username = input.username,
            email = input.email,
            password = passwordEncoder.encode(input.password),
            provider = "local",
            verificationCode = generateVerificationCode(),
            verificationExpiresAt = LocalDateTime.now().plusMinutes(15),
            enabled = false,
        )

        if(userRepository.findByEmail(input.email) != null)
            throw IllegalArgumentException("User with email \'${input.email}\' already exists")

        if(userRepository.findByUsername(input.username) != null)
            throw IllegalArgumentException("User with username \'${input.username}\' already exists")


        val role = roleRepository.findByName("ROLE_USER") ?: error("Unexpected behaviour during role creation")
        user.roles = listOf(role)
        sendVerificationEmail(user)

        return userRepository.save(user)
    }

    fun authenticate(input: LoginUserDto): User {
        val user = findUserByEmailOrUsername(input.email)

        if (!user.isEnabled) throw IllegalArgumentException("Account not verified. Please check your email.")

        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(user.username, input.password))
        } catch (e : AuthenticationException) {
            throw IllegalArgumentException("Password is incorrect")
        }
        return user
    }

    fun verifyUser(input: VerifyUserDto) {
        val user = findUserByEmailOrUsername(input.email)

        if (LocalDateTime.now() > user.verificationExpiresAt) {
            throw IllegalArgumentException("Verification code expired. Please sign up again.")
        }

        if (user.verificationCode != input.verificationCode) {
            throw IllegalArgumentException("Invalid verification code.")
        }

        user.enabled = true
        user.verificationCode = null
        user.verificationExpiresAt = null

        userRepository.save(user)
    }

    fun resendVerificationCode(email: String) {
        val user = findUserByEmailOrUsername(email)

        if (user.isEnabled) throw IllegalArgumentException("Account already verified.")

        user.verificationExpiresAt?.let {
            if(LocalDateTime.now().plusMinutes(14).plusSeconds(30) < it) return
        }
        user.verificationCode = generateVerificationCode()
        user.verificationExpiresAt = LocalDateTime.now().plusMinutes(15)

        sendVerificationEmail(user)

        userRepository.save(user)
    }

    fun sendVerificationEmail(user: User) {
        val subject = "Account verification"
        val htmlMessage = generateVerificationCodeEmail(user.verificationCode)
        try {
            emailService.sendVerificationEmail(user.email, subject, htmlMessage)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
    }

    private fun generateVerificationCode(): String {
        return (100000..999999).random().toString()
    }

    private fun findUserByEmailOrUsername(emailUsername: String?): User {
        if(emailUsername == null) throw IllegalArgumentException("User didn't provide email or username")
        return if (emailUsername.contains('@')) {
            userRepository.findByEmail(emailUsername)
                ?: throw IllegalArgumentException("User with email \"$emailUsername\" not found")
        } else {
            userRepository.findByUsername(emailUsername)
                ?: throw IllegalArgumentException("User with username \"$emailUsername\" not found")
        }
    }
}

private fun generateVerificationCodeEmail(verificationCode: String?): String = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Verification Code</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 0;
                        padding: 0;
                        background-color: #f4f4f4;
                    }
                    .email-container {
                        max-width: 600px;
                        margin: 20px auto;
                        background: #ffffff;
                        padding: 20px;
                        border-radius: 8px;
                        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                    }
                    .header {
                        text-align: center;
                        background: #007bff;
                        color: #ffffff;
                        padding: 10px 0;
                        border-radius: 8px 8px 0 0;
                    }
                    .content {
                        text-align: center;
                        padding: 20px;
                    }
                    .code {
                        display: inline-block;
                        font-size: 20px;
                        font-weight: bold;
                        color: #007bff;
                        background: #f4f4f4;
                        padding: 10px 20px;
                        margin: 20px 0;
                        border: 1px dashed #007bff;
                        border-radius: 4px;
                    }
                    .button {
                        display: inline-block;
                        margin-top: 20px;
                        padding: 10px 20px;
                        color: #ffffff;
                        background: #007bff;
                        text-decoration: none;
                        border-radius: 4px;
                        font-size: 16px;
                    }
                    .footer {
                        margin-top: 20px;
                        font-size: 12px;
                        color: #888888;
                        text-align: center;
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <h1>Verify Your Email</h1>
                    </div>
                    <div class="content">
                        <p>Thank you for signing up! Use the verification code below to verify your email address:</p>
                        <div class="code">${verificationCode}</div>
                        <p>If you didn't request this, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2024 Your Company Name. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()