package repository.interfaces

import domain.User
import domain.UserRanking
import java.util.UUID

interface UsersRepository {

    fun createUser(
        username: String,
        password_hash: String
    ): Boolean

    fun updateUser(user: User): Boolean

    fun getUserByUsername(username: String): User?

    fun userExistsByUsername(username: String): Boolean


    fun getUserByToken(token: UUID): User?

    fun createToken(token: UUID, username: String): Boolean
}