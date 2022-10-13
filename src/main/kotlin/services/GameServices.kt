package services

import ActionResult
import domain.*
import repository.jdbi.JdbiGamesRepository
import java.util.*

class GameServices(
    private val repository: JdbiGamesRepository
): GameServicesInterface {
    override fun makeShot(game_id: UUID, newShot: Shot): Boolean {
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

sealed class MakeShotsError{
    object NotShootingPhase: MakeShotsError()
    object NotYourTurn: MakeShotsError()
    object RepeatedShot: MakeShotsError()
}




