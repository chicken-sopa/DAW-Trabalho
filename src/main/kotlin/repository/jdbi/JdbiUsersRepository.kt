package repository.jdbi

import domain.User
import domain.UserRanking
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import repository.interfaces.UsersRepository
import java.util.*

class JdbiUsersRepository (
    private val handle: Handle
): UsersRepository {

    override fun create(user: User): Boolean =
        handle.createUpdate(
            """
               insert into users
               (username, password_hash)
                values
               (:username, :password)
            """
        )
            .bind("username", user.username)
            .bind("password", user.password_hash)
            .execute() == 1

    override fun update(user: User): Boolean =
        handle.createUpdate(
            """
               update users set
               password_hash = :password_hash, 
               games_played = :games_played, games_won = :games_won, ranking_points = :ranking_points
               
               where username = :username
            """
        )
            .bind("password_hash", user.password_hash)
            .bind("games_played", user.games_played)
            .bind("games_won", user.games_won)
            .bind("ranking_points", user.ranking_points)
            .execute() == 1

    override fun getByUsername(username: String): User? =
        handle.createQuery(
            """
               select * from users where username = :username
            """
        )
            .bind("username", username)
            .mapTo<User>()
            .singleOrNull()

    override fun getUserByToken(token: String): User? =
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
               insert into tokens
               (token_value, username)
                values
               (:token, :username) 
            """
        )
            .bind("token", token)
            .bind("username", username)
            .execute() == 1
}