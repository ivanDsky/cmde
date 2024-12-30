package ua.ivandsky.cmde.model

import jakarta.persistence.*

@Entity
data class Privilege(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,
    val name: String = "",
) {
    @ManyToMany(mappedBy = "privileges")
    val roles: Collection<Role> = emptyList()
}