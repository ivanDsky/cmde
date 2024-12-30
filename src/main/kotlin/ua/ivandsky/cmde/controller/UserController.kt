package ua.ivandsky.cmde.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ua.ivandsky.cmde.model.User
import ua.ivandsky.cmde.response.UserResponse
import ua.ivandsky.cmde.response.toUserResponse
import ua.ivandsky.cmde.service.UserService

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/all")
    fun getAllUsers(): ResponseEntity<List<UserResponse>> =
        ResponseEntity.ok(userService.allUsers().map { it.toUserResponse() })

    @GetMapping("/")
    fun getAuthenticatedUser(): ResponseEntity<UserResponse> {
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
        val currentUser = userDetails as User
        return ResponseEntity.ok(currentUser.toUserResponse())
    }
}