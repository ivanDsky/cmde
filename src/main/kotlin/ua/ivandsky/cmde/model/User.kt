package ua.ivandsky.cmde.model

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
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

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "users_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    var roles: Collection<Role> = emptyList()
    override fun getAuthorities() = roles.flatMap { it.authorities }

    override fun getUsername(): String = username ?: email

    override fun getPassword(): String? = password
    override fun isEnabled(): Boolean = enabled
}

private val Role.authorities: List<GrantedAuthority>
    get() = privileges.map { SimpleGrantedAuthority(it.name) } +
            SimpleGrantedAuthority(name)