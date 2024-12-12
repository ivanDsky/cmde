package ua.ivandsky.cmde.service

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import ua.ivandsky.cmde.dto.Oauth2UserInfoDto
import ua.ivandsky.cmde.model.User
import ua.ivandsky.cmde.repository.UserRepository

@Service
class OAuth2UserService(
    private val userRepository: UserRepository
) : DefaultOAuth2UserService() {
    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(oAuth2UserRequest)
        processOAuth2User(oAuth2UserRequest, oAuth2User)
        return oAuth2User
    }

    private fun processOAuth2User(oAuth2UserRequest: OAuth2UserRequest, oAuth2User: OAuth2User) {
        val userInfoDto = Oauth2UserInfoDto(
            id = oAuth2User.attributes["sub"].toString(),
            username = oAuth2User.attributes["name"].toString(),
            email = oAuth2User.attributes["email"].toString()
        )

        val user: User? = userRepository.findByEmail(userInfoDto.email)

        if (user != null) {
            updateExistingUser(user, userInfoDto)
        } else {
            registerNewUser(oAuth2UserRequest, userInfoDto)
        }
    }

    private fun registerNewUser(oAuth2UserRequest: OAuth2UserRequest, userInfoDto: Oauth2UserInfoDto): User {
        val user = User(
            username = userInfoDto.username,
            email = userInfoDto.email,
            provider = oAuth2UserRequest.clientRegistration.registrationId,
            enabled = true,
        )
        return userRepository.save(user)
    }

    fun updateExistingUser(existingUser: User, userInfoDto: Oauth2UserInfoDto): User {
        return existingUser
        val updatedUser = existingUser.copy(
            username = userInfoDto.username
        )
        return userRepository.save(updatedUser)
    }

}