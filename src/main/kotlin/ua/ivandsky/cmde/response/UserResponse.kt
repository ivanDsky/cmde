package ua.ivandsky.cmde.response

import ua.ivandsky.cmde.model.User
import java.time.LocalDateTime

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val enabled: Boolean,
    val verificationExpiresAt: LocalDateTime?
)

fun User.toUserResponse(): UserResponse =
    UserResponse(
        id = id,
        username = username,
        email = email,
        enabled = enabled,
        verificationExpiresAt = verificationExpiresAt
    )