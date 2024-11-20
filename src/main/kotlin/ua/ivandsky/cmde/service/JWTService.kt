package ua.ivandsky.cmde.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JWTService(
    @Value("\${security.jwt.secret}")
    private val jwtSecret: String,
    @Value("\${security.jwt.expiration}")
    val jwtExpiration: Long,
) {
    fun extractUsername(token: String): String = extractClaim(token) { it.subject }
    fun extractExpiration(token: String): Date = extractClaim(token) { it.expiration }

    fun generateToken(userDetails: UserDetails): String = buildToken(mutableMapOf(), userDetails, jwtExpiration)

    fun buildToken(extraClaims: Map<String, Any>, userDetails: UserDetails, expirationTime: Long): String =
        Jwts.builder().apply {
            claims(extraClaims)
            subject(userDetails.username)
            issuedAt(Date(System.currentTimeMillis()))
            expiration(Date(System.currentTimeMillis() + expirationTime))
            signWith(signInKey)
        }.compact()

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    fun isTokenExpired(token: String): Boolean {
        val currentTime = Date(System.currentTimeMillis())
        val expirationTime = extractExpiration(token)
        return expirationTime < currentTime
    }

    private fun<T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(signInKey)
            .build()
            .parseSignedClaims(token)
            .payload

    private val signInKey: SecretKey
        get() {
            val keyBytes = Decoders.BASE64.decode(jwtSecret)
            return Keys.hmacShaKeyFor(keyBytes)
        }
}