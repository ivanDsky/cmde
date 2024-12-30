package ua.ivandsky.cmde.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import ua.ivandsky.cmde.service.OAuth2UserService

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val authenticationProvider: AuthenticationProvider,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val oauth2UserService: OAuth2UserService,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .headers { headers -> headers.frameOptions { it.disable() } }
            .authorizeHttpRequests {
                it.requestMatchers("/")
                    .permitAll()
                it.requestMatchers("/auth/**")
                    .permitAll()
                it.requestMatchers("/user/all")
                    .hasRole("ADMIN")
                it.anyRequest()
                    .authenticated()
            }
//            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .oauth2Login { customizer ->
                customizer
                    .userInfoEndpoint{ it.userService(oauth2UserService) }
            }
            .formLogin {
                it.defaultSuccessUrl("/secured", true)
            }
            .logout {
                it.deleteCookies("remove")
                    .invalidateHttpSession(false)
                    .logoutSuccessUrl("/")
            }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        configuration.apply {
            allowedOriginPatterns = listOf("http://localhost:8080")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
            allowedHeaders = listOf("Authorization", "Content-Type")
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return source
    }
}