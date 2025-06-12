package com.project.quizcafe.domain.auth.security.oauth

import com.project.quizcafe.domain.auth.validator.EmailValidator
import com.project.quizcafe.domain.user.entity.User
import com.project.quizcafe.domain.user.repository.UserRepository
import com.project.quizcafe.domain.user.validator.UserValidator
import org.springframework.security.oauth2.client.userinfo.*
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
    private val emailValidator: EmailValidator
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val delegate = DefaultOAuth2UserService()
        val oAuth2User = delegate.loadUser(userRequest)

        val attributes = oAuth2User.attributes
        val provider = userRequest.clientRegistration.registrationId
        val email = attributes["email"] as String
        val nickName = attributes["name"] as String
        val providerEnum = User.Provider.valueOf(provider.uppercase())

        emailValidator.validateEmailExist(email)
        val newUser = User.createOAuthUser(email, nickName, providerEnum)
        val user = userRepository.save(newUser)

        return CustomOAuth2User(user.loginEmail, user.role.name, attributes)
    }
}
