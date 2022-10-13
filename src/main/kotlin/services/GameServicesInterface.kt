package services

import domain.Game
import domain.Player
import domain.Ship
import domain.Shot
import java.util.*

interface GameServicesInterface {

    fun makeShots(game_id: UUID, newShots: Shot): Boolean

    fun createLayout(game_id: UUID, listOfShips: List<Ship>): Boolean

    //fun createGame(gameIdPlayer1 : UUID)

    fun getGame(game_id: UUID): Game

    fun getCurrentTurn(game_id: UUID):Player
}