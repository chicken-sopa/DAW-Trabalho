package services.logic

import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

@Component
class TokenLogic {

    fun generateToken(): String =
        ByteArray(TOKEN_BYTE_SIZE).let { byteArray ->
            SecureRandom.getInstanceStrong().nextBytes(byteArray)
            Base64.getUrlEncoder().encodeToString(byteArray)
        }

    fun canBeToken(token: String): Boolean = try {
        Base64.getUrlDecoder()
            .decode(token).size == TOKEN_BYTE_SIZE
    } catch (ex: IllegalArgumentException) {
        false
    }

    fun hashToken(token: String): String {
        val messageDigest = MessageDigest.getInstance("SHA256")
        return Base64.getUrlEncoder().encodeToString(
            messageDigest.digest(
                Charsets.UTF_8.encode(token).array()
            )
        )
    }

    fun verifyToken(raw: String, original_hash: String): Boolean =
        hashToken(raw) == original_hash

    companion object {
        private const val TOKEN_BYTE_SIZE = 256 / 8
    }
}