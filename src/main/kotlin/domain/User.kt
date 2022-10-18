package domain

data class PasswordValidationError(override val message: String): Throwable()

/**
 * PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("Password123")
 * */
data class User(
    val username: String,
    val password_hash: String,
    val games_played: Int,
    val games_won: Int,
    val ranking_points: Int = 0
) {
    init {
        require(username.length in 3..20)
        require(password_hash.length == 68)
    }

    companion object {

        fun validatePassword(password: String) {
            if(password.length !in 5..20)
                throw PasswordValidationError("Invalid password Length! (5 < size < 20)")
            if (password.none { it.isLowerCase() })
                throw PasswordValidationError("Password must contain lowercase letters!")
            if (password.none { it.isUpperCase() })
                throw PasswordValidationError("Password must contain uppercase letters!")
            if (password.none { it.isDigit() })
                throw PasswordValidationError("Password must contain numbers!")
        }

        fun validateUsername(username: String) {
            if (username.length !in 3..20)
                throw PasswordValidationError("Invalid username size! (3 < size > 20)")
            if (username.any { !it.isLetterOrDigit() })
                throw PasswordValidationError("Username must only contain letters and digits")
        }
    }
}
