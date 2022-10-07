package domain

data class User(val username: String, val password_hash: String, val ranking_points: Int = 0)
