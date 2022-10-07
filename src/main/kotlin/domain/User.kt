package domain

data class User(val username: String, val password: String, val ranking_points: Int = 0)
