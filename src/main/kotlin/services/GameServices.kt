package services

import domain.game.*
import repository.TransactionManager
import repository.jdbi.JdbiGamesRepository
import services.interfaces.IGameServices
import java.util.*

class GameServices(
    private val transactionManager: TransactionManager
): IGameServices {

    override fun submitFleet(token: UUID, game_id: UUID, fleet: List<Ship>): Boolean {
        TODO("Not yet implemented")
    }

    override fun makeShot(token: UUID, game_id: UUID, shot: Shot): Game {
        val game = repository.getById(game_id)
        // require that exists a game in database
        requireNotNull(game)

        // updateGame and save in database
        val resultOfMakeShot = game.makeShot(game.turn, newShots)

        if(resultOfMakeShot is ActionResult.Success){
            val updatedGame = resultOfMakeShot.value
            val update = repository.update(updatedGame)

            // if bd update does not go right the send error
            if(!update){

            }
        }

        return true
    }

    override fun getMyFleet(token: UUID, game_id: UUID): List<Ship> {
        TODO("Not yet implemented")
    }

    override fun getOpponentFleet(token: UUID, game_id: UUID): List<OpponentShip> {
        TODO("Not yet implemented")
    }

    override fun getGame(token: UUID, game_id: UUID): Game {
        TODO("Not yet implemented")
    }

}




