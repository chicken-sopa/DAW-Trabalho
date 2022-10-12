package repository

import domain.*
import org.jdbi.v3.core.Handle
import org.junit.jupiter.api.Test
import repository.jdbi.JdbiGamesRepository
import repository.jdbi.JdbiUsersRepository
import repository.jdbi.utils.gson
import utils.testWithHandleAndRollback
import java.util.*

class GamesTests {

    private val TEST_USER_1 = User("TEST_USER_1", "password_hash_simulation")
    private val TEST_USER_2 = User("TEST_USER_2", "password_hash_simulation")

    private fun prepareTestEnvironment(handle: Handle) {
        val usersRepo = JdbiUsersRepository(handle)

        usersRepo.createUser(
            TEST_USER_1.username,
            TEST_USER_1.password_hash
        )
        assert(usersRepo.getUserByUsername(TEST_USER_1.username) != null)

        usersRepo.createUser(
            TEST_USER_2.username,
            TEST_USER_2.password_hash
        )
        assert(usersRepo.getUserByUsername(TEST_USER_2.username) != null)
    }

    @Test
    fun `Create and retrieve game without information loss`()  {
        testWithHandleAndRollback { handle ->

            prepareTestEnvironment(handle)

            val gamesRepository = JdbiGamesRepository(handle)
            val newGameID = UUID.randomUUID()

            val sutGame = Game(
                newGameID,
                GameRules(),
                TEST_USER_1.username,
                TEST_USER_2.username,
            )
            val createGameOperation = gamesRepository.create(sutGame)
            assert(createGameOperation)

            val remoteGame = gamesRepository.getById(newGameID)

            assert(remoteGame == sutGame)
            // Compare the json contents as well
            assert(gson().toJson(remoteGame) == gson().toJson(sutGame))
        }
    }
}