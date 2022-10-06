package repository

import domain.User
import java.util.UUID

interface UsersRepository {

    fun createUser(
        username: String,
        password: String,
        ranking_points: Int
    ): Boolean

    fun getUserByUsername(username: String): User?

    fun userExistsByUsername(username: String): Boolean

    fun getUserByToken(token_value: UUID): User?

    fun createToken(token_value: UUID, username: String): Boolean

    fun getUserRankingPointsByUsername(username: String): Int
}