package repository.interfaces

import domain.User
import domain.UserRanking
import java.util.UUID

interface UsersRepository {

    fun create(user: User): Boolean

    fun update(user: User): Boolean

    fun getByUsername(username: String): User?

    fun getUserByToken(token: String): User?

    fun createToken(token: String, username: String): Boolean

}