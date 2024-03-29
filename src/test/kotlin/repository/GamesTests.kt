package repository

import domain.game.Game
import org.jdbi.v3.core.Handle
import org.junit.jupiter.api.Test
import repository.jdbi.JdbiGamesRepository
import repository.jdbi.JdbiUsersRepository
import repository.jdbi.utils.gson
import utils.testGameMode
import utils.testWithHandleAndRollback
import java.util.*
import kotlin.test.assertNotNull

class GameTests {

    private val username1 = "TESTUSER1"
    private val username2 = "TESTUSER2"

    private fun checkDBEnvironment(handle: Handle) {
        val usersRepo = JdbiUsersRepository(handle)

        assertNotNull(usersRepo.getByUsername(username1))
        assertNotNull(usersRepo.getByUsername(username2))
    }

    @Test
    fun `Create and retrieve game without information loss`()  {
        testWithHandleAndRollback { handle ->

            checkDBEnvironment(handle)

            val gamesRepository = JdbiGamesRepository(handle)
            val newGameID = UUID.randomUUID()

            val sutGame = Game(
                newGameID,
                testGameMode,
                username1,
                username2,
            )
            val createGameOperation = gamesRepository.create(sutGame)
            assert(createGameOperation)

            val remoteGame = gamesRepository.getById(newGameID)

            println(remoteGame)
            println(sutGame)

            assert(remoteGame == sutGame)
            // Compare the json contents as well
            assert(gson().toJson(remoteGame) == gson().toJson(sutGame))
        }
    }
}