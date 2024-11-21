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
import ua.ivandsky.cmde.service.UserService

@Configuration
class ApplicationConfig(
    private val userService: UserService,
    private val config: AuthenticationConfiguration,
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(): AuthenticationManager = config.authenticationManager

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()

        authProvider.apply {
            setUserDetailsService(userService)
            setPasswordEncoder(passwordEncoder())
        }

        return authProvider
    }
}