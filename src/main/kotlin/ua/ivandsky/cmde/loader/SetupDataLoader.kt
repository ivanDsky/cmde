package ua.ivandsky.cmde.loader

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ua.ivandsky.cmde.model.Privilege
import ua.ivandsky.cmde.model.Role
import ua.ivandsky.cmde.model.User
import ua.ivandsky.cmde.repository.PrivilegeRepository
import ua.ivandsky.cmde.repository.RoleRepository
import ua.ivandsky.cmde.repository.UserRepository


@Component
class SetupDataLoader(
    private val privilegeRepository: PrivilegeRepository,
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : ApplicationRunner {
    @Transactional
    override fun run(args: ApplicationArguments?) {
        if (privilegeRepository.count() > 0) {
            return
        }

        // == create initial privileges
        val readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE")
        val writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE")


        // == create initial roles
        val adminPrivileges = listOf(readPrivilege, writePrivilege)
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges)
        createRoleIfNotFound("ROLE_USER", emptyList())

        val adminRole: Role = roleRepository.findByName("ROLE_ADMIN") ?: error("Unexpected behaviour during role creation")
        val admin = User(
            username = "admin",
            password = passwordEncoder.encode("admin"),
            email = "admin@test.com",
            enabled = true,
        )
        admin.roles = listOf(adminRole)
        userRepository.save(admin)

        val basicRole: Role = roleRepository.findByName("ROLE_USER") ?: error("Unexpected behaviour during role creation")
        val basicUser = User(
            username = "basicUser",
            password = passwordEncoder.encode("user"),
            email = "user@test.com",
            enabled = true
        )
        basicUser.roles = listOf(basicRole)
        userRepository.save(basicUser)
    }

    @Transactional
    private fun createPrivilegeIfNotFound(name: String): Privilege {
        var privilege: Privilege? = privilegeRepository.findByName(name)
        if (privilege == null) {
            privilege = Privilege(name = name)
            privilegeRepository.save(privilege)
        }
        return privilege
    }

    @Transactional
    private fun createRoleIfNotFound(name: String, privileges: Collection<Privilege>): Role {
        var role: Role? = roleRepository.findByName(name)
        if (role == null) {
            role = Role(name = name)
            role.privileges = privileges
            roleRepository.save(role)
        }
        return role
    }
}