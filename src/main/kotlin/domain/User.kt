package domain

data class User(
    val username: String,
    val password_hash: String,
    val games_played: Int,
    val games_won: Int,
    val ranking_points: Int = 0
)
