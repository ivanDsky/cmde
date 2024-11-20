package ua.ivandsky.cmde.dto

data class RegisterUserDto(
    val email: String,
    val username: String,
    val password: String,
)