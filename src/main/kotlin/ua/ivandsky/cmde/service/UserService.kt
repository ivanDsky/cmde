package ua.ivandsky.cmde.service

import org.springframework.stereotype.Service
import ua.ivandsky.cmde.model.User
import ua.ivandsky.cmde.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val emailService: EmailService,
) {
    fun allUsers(): List<User> = userRepository.findAll()
}