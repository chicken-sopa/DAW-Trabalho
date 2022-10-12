package domain

data class User(
    val username: String,
    val password_hash: String,
    val games_won: Int,
    val games_player: Int,
    val ranking_points: Int = 0
)
