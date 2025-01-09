package ua.ivandsky.cmde.service

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import ua.ivandsky.cmde.dto.Oauth2UserInfoDto
import ua.ivandsky.cmde.model.User
import ua.ivandsky.cmde.repository.UserRepository

@Component
class OAuthSuccessHandler(
    @Autowired private val userRepository: UserRepository,
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        if(authentication?.principal is OidcUser) processOAuth2User(authentication.principal as OidcUser)
        response?.sendRedirect("/secured")
    }

    private fun processOAuth2User(oidcUser: OidcUser) {
        val userInfoDto = Oauth2UserInfoDto(
            id = oidcUser.attributes["sub"].toString(),
            username = oidcUser.attributes["name"].toString(),
            email = oidcUser.attributes["email"].toString()
        )

        val user: User? = userRepository.findByEmail(userInfoDto.email)

        if (user != null) {
            updateExistingUser(user, userInfoDto)
        } else {
            registerNewUser(userInfoDto)
        }
    }

    private fun registerNewUser(userInfoDto: Oauth2UserInfoDto): User {
        val user = User(
            username = userInfoDto.username,
            email = userInfoDto.email,
            provider = "google",
            enabled = true,
        )
        return userRepository.save(user)
    }

    fun updateExistingUser(existingUser: User, userInfoDto: Oauth2UserInfoDto): User {
        return existingUser
//        val updatedUser = existingUser.copy(
//            username = userInfoDto.username
//        )
//        return userRepository.save(updatedUser)
    }
}