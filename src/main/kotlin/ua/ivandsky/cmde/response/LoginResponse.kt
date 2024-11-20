package ua.ivandsky.cmde.response

data class LoginResponse(
    val token: String,
    val expiresIn: Long,
)
