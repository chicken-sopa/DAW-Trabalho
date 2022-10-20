package services

import domain.GameMode
import domain.game.*
import repository.TransactionManager
import services.interfaces.IGameServices
import java.util.*

class GameServices(
    private val transactionManager: TransactionManager
): IGameServices {

    override fun submitFleet(username: String, game_id: UUID, fleet: List<Ship>): Boolean {
        TODO("Not yet implemented")
    }

    override fun makeShot(username: String, game_id: UUID, shot: Shot): Boolean {
        return transactionManager.run {
            val gamesRepo = it.gamesRepository
            val game = gamesRepo.getById(game_id)
            // TODO: throw GameNotFound later
            requireNotNull(game)

            // val username = it.userRepository.getByToken(token)
            // val player = game.whichPlayer(username)
            // pass "player" to game.makeShot(player, shot)

            // updateGame and save in database
            // val resultOfMakeShot = game.makeShot(game.turn, shot)

            /*if (resultOfMakeShot is Result.Success<*>) {
                val updatedGame = resultOfMakeShot.value
                //val updateResult = gamesRepo.update(updatedGame)

                // if bd update does not go right the send error
                //if (!updateResult) {
                    // Error Updating Remote Game
                //}
            }*/
            true
        }
    }

    override fun getMyFleet(username: String, game_id: UUID): List<Ship> {
        TODO("Not yet implemented")
    }

    override fun getOpponentFleet(username: String, game_id: UUID): List<OpponentShip> {
        TODO("Not yet implemented")
    }

    override fun get(username: String, game_id: UUID): LimitedGame {
        TODO("Not yet implemented")
    }

    override fun forfeit(username: String, game_id: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun getGameModes(): List<GameMode> {
        TODO("Not yet implemented")
    }
}




