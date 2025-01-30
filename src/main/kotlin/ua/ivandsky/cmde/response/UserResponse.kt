package ua.ivandsky.cmde.response

import ua.ivandsky.cmde.model.Role
import ua.ivandsky.cmde.model.User
import java.time.LocalDateTime

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val avatar: String,
    val enabled: Boolean,
    val roles: List<String>,
    val verificationExpiresAt: LocalDateTime?
)

fun User.toUserResponse(): UserResponse =
    UserResponse(
        id = id,
        username = username,
        email = email,
        enabled = enabled,
        avatar = avatar,
        roles = roles.map { it.toResponse() },
        verificationExpiresAt = verificationExpiresAt
    )

private fun Role.toResponse(): String =
    name.removePrefix("ROLE_").run {
        first() + substring(1).lowercase()
    }