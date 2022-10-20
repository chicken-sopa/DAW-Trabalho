package domain

/**
 * PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("Password123")
 * */
data class User(
    val username: String,
    val password_hash: String,
    val games_played: Int = 0,
    val games_won: Int = 0,
    val ranking_points: Int = 0
)
