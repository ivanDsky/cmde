package ua.ivandsky.cmde.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
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
        val currentUser = authentication.principal as User
        return ResponseEntity.ok(currentUser.toUserResponse())
    }
}