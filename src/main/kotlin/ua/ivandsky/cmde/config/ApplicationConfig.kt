package ua.ivandsky.cmde.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import ua.ivandsky.cmde.repository.UserRepository

@Configuration
class ApplicationConfig(
    private val userRepository: UserRepository
) {
    @Bean
    fun userDetailsService(): UserDetailsService = UserDetailsService { email: String ->
        userRepository.findByEmail(email) ?:
            throw UsernameNotFoundException("User with email: $email not found")
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager = config.authenticationManager

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()

        authProvider.apply {
            setUserDetailsService(userDetailsService())
            setPasswordEncoder(passwordEncoder())
        }

        return authProvider
    }
}