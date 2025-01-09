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
import ua.ivandsky.cmde.usecase.GetAuthenticatedUserUseCase

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
    private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase
) {
    @GetMapping("/all")
    fun getAllUsers(): ResponseEntity<List<UserResponse>> =
        ResponseEntity.ok(userService.allUsers().map { it.toUserResponse() })

    @GetMapping("/")
    fun getAuthenticatedUser(): ResponseEntity<UserResponse> {
        val currentUser = getAuthenticatedUserUseCase() ?:
            return ResponseEntity.notFound().build()
        return ResponseEntity.ok(currentUser.toUserResponse())
    }
}