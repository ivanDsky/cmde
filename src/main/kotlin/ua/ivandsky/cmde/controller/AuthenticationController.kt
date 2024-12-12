package ua.ivandsky.cmde.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ua.ivandsky.cmde.dto.LoginUserDto
import ua.ivandsky.cmde.dto.RegisterUserDto
import ua.ivandsky.cmde.dto.VerifyUserDto
import ua.ivandsky.cmde.model.User
import ua.ivandsky.cmde.response.LoginResponse
import ua.ivandsky.cmde.response.UserResponse
import ua.ivandsky.cmde.response.toUserResponse
import ua.ivandsky.cmde.service.AuthenticationService
import ua.ivandsky.cmde.service.JWTService

@RestController
@RequestMapping("/auth")
class AuthenticationController(
    private val jwtService: JWTService,
    private val authenticationService: AuthenticationService,
) {
     @PostMapping("/sign-up")
     fun signUp(@RequestBody input: RegisterUserDto): ResponseEntity<UserResponse> {
         val user = authenticationService.signUp(input)
         return ResponseEntity.ok(user.toUserResponse())
     }

     @PostMapping("/login")
     fun signIn(@RequestBody input: LoginUserDto): ResponseEntity<LoginResponse> {
         val user = authenticationService.authenticate(input)
         val token = jwtService.generateToken(user)
         val expiresIn = jwtService.jwtExpiration
         return ResponseEntity.ok(LoginResponse(token, expiresIn))
     }

     @PostMapping("/verify")
     fun verify(@RequestBody input: VerifyUserDto): ResponseEntity<Any> {
         try {
                authenticationService.verifyUser(input)
             return ResponseEntity.ok("Response verified successfully")
            } catch (e: IllegalArgumentException) {
                return ResponseEntity.badRequest().body(e.message)
         }
     }

    @PostMapping("/resend")
    fun resendVerificationCode(@RequestBody email: String): ResponseEntity<Any> {
        try {
            authenticationService.resendVerificationCode(email)
            return ResponseEntity.ok("Verification code has been sent")
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<Any> {
        try {
            SecurityContextHolder.clearContext()
            return ResponseEntity.ok("Logged out successfully")
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

}