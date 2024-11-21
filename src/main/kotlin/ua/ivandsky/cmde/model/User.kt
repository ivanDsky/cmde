package ua.ivandsky.cmde.model

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    private val username: String = "",

    @Column(unique = true)
    val email: String = "",

    private val password: String = "",
    var enabled: Boolean = true,
    var verificationCode: String? = null,
    var verificationExpiresAt: LocalDateTime? = null
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()

    override fun getUsername(): String = username

    override fun getPassword(): String = password
    override fun isEnabled(): Boolean = enabled
}