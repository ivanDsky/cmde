package ua.ivandsky.cmde.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ua.ivandsky.cmde.model.User
import ua.ivandsky.cmde.repository.UserRepository
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val userRepository: UserRepository
): UserDetailsService {
    fun findById(id: Long): User? = userRepository.findById(id).getOrNull()
    fun allUsers(): List<User> = userRepository.findAll()

    override fun loadUserByUsername(usernameOrEmail: String): UserDetails {
        val user = if (usernameOrEmail.contains("@")) {
            userRepository.findByEmail(usernameOrEmail) ?:
                throw UsernameNotFoundException("User with email: $usernameOrEmail not found")
        } else {
            userRepository.findByUsername(usernameOrEmail) ?:
                throw UsernameNotFoundException("User with username: $usernameOrEmail not found")
        }

        return user
    }
}