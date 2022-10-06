package repository

import domain.User
import java.util.UUID

interface UsersRepository {

    fun createUser(
        username: String,
        password: String
    ): Boolean

    fun getUserByUsername(username: String): User?

    fun userExistsByUsername(username: String): Boolean

    fun getUserByToken(token: UUID): User?

    fun createToken(token: UUID, username: String): Boolean

    fun getUserRankingPointsByUsername(username: String): Int?
}