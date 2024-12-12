package ua.ivandsky.cmde.model

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    private val username: String? = null,

    @Column(unique = true)
    val email: String = "",

    private val password: String? = null,

    val provider: String? = null,

    var enabled: Boolean = false,
    var verificationCode: String? = null,
    var verificationExpiresAt: LocalDateTime? = null
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()

    override fun getUsername(): String = username ?: email

    override fun getPassword(): String? = password
    override fun isEnabled(): Boolean = enabled
}