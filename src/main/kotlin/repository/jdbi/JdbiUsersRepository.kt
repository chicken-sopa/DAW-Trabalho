package repository.jdbi

import domain.User
import org.jdbi.v3.core.Handle
import repository.UsersRepository
import java.util.*

class JdbiUsersRepository (
    private val handle: Handle
): UsersRepository{

    override fun createUser(username: String, password: String, ranking_points: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun getUserByUsername(username: String): User? {
        TODO("Not yet implemented")
    }

    override fun userExistsByUsername(username: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getUserByToken(token_value: UUID): User? {
        TODO("Not yet implemented")
    }

    override fun createToken(token_value: UUID, username: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getUserRankingPointsByUsername(username: String): Int {
        TODO("Not yet implemented")
    }
}