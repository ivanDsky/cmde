package ua.ivandsky.cmde.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ua.ivandsky.cmde.service.UserService
import ua.ivandsky.cmde.usecase.GetAuthenticatedUserUseCase

@RestController
@RequestMapping("/")
class HomeController(
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase
) {
    @RequestMapping
    fun home(): String = "Welcome to CMDE"

    @RequestMapping("/secured")
    fun secured(): ResponseEntity<String> {
        val currentUser = getAuthenticatedUserUseCase() ?:
            return ResponseEntity.notFound().build()
        return ResponseEntity.ok("Welcome to authed page ${currentUser.username}")
    }
}