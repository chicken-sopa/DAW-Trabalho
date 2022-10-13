package services

import domain.Game
import domain.Player
import domain.Ship
import domain.Shot
import repository.jdbi.JdbiGamesRepository
import java.util.*

class GameServices(
    private val repository: JdbiGamesRepository
): GameServicesInterface {
    override fun makeShots(game_id: UUID, newShots: Set<Shot>): Game {
        /*val game = repository.getById(game_id)
        // require that exists a game in database
        requireNotNull(game)

        // updateGame and save in database
        val updatedGame = game.makeShots(game.turn, newShots)
        val update = repository.update(updatedGame)

        return updatedGame*/
        TODO()

    }

    override fun createLayout(game_id: UUID, listOfShips: List<Ship>): Boolean {
        TODO("Not yet implemented")
    }

    override fun getGame(game_id: UUID): Game {
        TODO("Not yet implemented")
    }

    override fun getCurrentTurn(game_id: UUID): Player {
        TODO("Not yet implemented")
    }
}