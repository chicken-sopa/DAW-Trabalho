package repository.jdbi

import domain.User
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.springframework.stereotype.Repository
import repository.UsersRepository
import java.util.*

class JdbiUsersRepository (
    private val handle: Handle
): UsersRepository {

    override fun createUser(username: String, password_hash: String): Boolean =
        handle.createUpdate(
            """
               insert into users(username, password_hash) values
               (:username, :password)
            """
        )
            .bind("username", username)
            .bind("password", password_hash)
            .execute() == 1

    override fun getUserByUsername(username: String): User? =
        handle.createQuery(
            """
               select * from users where username = :username
            """
        )
            .bind("username", username)
            .mapTo<User>()
            .singleOrNull()

    override fun userExistsByUsername(username: String): Boolean =
        handle.createQuery(
            """
               select count(*) from users where username = :username
            """
        )
            .bind("username", username)
            .mapTo<Int>()
            .single() == 1

    override fun getUserByToken(token: UUID): User? =
        handle.createQuery(
            """
               select u.username, u.password_hash, u.ranking_points
               from users u, tokens t
               where t.token_value = :token
            """
        )
            .bind("token", token)
            .mapTo<User>()
            .singleOrNull()

    override fun createToken(token: UUID, username: String): Boolean =
        handle.createUpdate(
            """
               insert into tokens(token_value, username) values
               (:token, :username) 
            """
        )
            .bind("token", token)
            .bind("username", username)
            .execute() == 1

    override fun getUserRankingPointsByUsername(username: String): Int? =
        handle.createQuery(
            """
               select ranking_points from users
               where username = :username
            """
        )
            .bind("username", username)
            .mapTo<Int>()
            .singleOrNull()
}