package ua.ivandsky.cmde.controller

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ua.ivandsky.cmde.service.UserService

@RestController
@RequestMapping("/")
class HomeController(
    private val userService: UserService,
) {
    @RequestMapping
    fun home(): String = "Welcome to CMDE"

    @RequestMapping("/secured")
    fun secured(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val userDetails: UserDetails
        if (authentication.principal is OAuth2User) {
            val oAuth2User = authentication.principal as OAuth2User
            val email = oAuth2User.attributes["email"] as? String
            val username = oAuth2User.attributes["name"] as String
            userDetails =
                if (email.isNullOrBlank()) userService.loadUserByUsername(username)
                else userService.loadUserByUsername(email)
        } else {
            userDetails = authentication.principal as UserDetails
        }
        return "Welcome to authed page ${userDetails.username}"
    }
}