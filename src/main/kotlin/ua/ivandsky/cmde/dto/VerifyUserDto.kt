package ua.ivandsky.cmde.dto

data class VerifyUserDto(
    val email: String,
    val verificationCode: String
)