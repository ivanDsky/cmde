package ua.ivandsky.cmde.usecase

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component
import ua.ivandsky.cmde.model.User
import ua.ivandsky.cmde.service.UserService

@Component
class GetAuthenticatedUserUseCase(
    @Autowired private val userService: UserService,
) {
    operator fun invoke(): User? {
        val authentication = SecurityContextHolder.getContext().authentication
        val userDetails: UserDetails?
        if (authentication.principal is OAuth2User) {
            val oAuth2User = authentication.principal as OAuth2User
            val email = oAuth2User.attributes["email"] as? String
            val username = oAuth2User.attributes["name"] as String
            userDetails =
                if (email.isNullOrBlank()) userService.loadUserByUsername(username)
                else userService.loadUserByUsername(email)
        } else {
            userDetails = authentication.principal as? UserDetails
        }
        val currentUser = userDetails as? User
        return currentUser
    }
}