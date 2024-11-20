package ua.ivandsky.cmde.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ua.ivandsky.cmde.model.User

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByUsername(username: String): User?
    fun findByVerificationCode(verificationCode: String): User?
}