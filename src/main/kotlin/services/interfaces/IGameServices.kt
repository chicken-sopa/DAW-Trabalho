package services.interfaces

import domain.game.Game
import domain.game.OpponentShip
import domain.game.Ship
import domain.game.Shot
import java.util.*

interface IGameServices {

    // Allow an user to define the layout of their fleet in the grid.
    fun submitFleet(token: UUID, game_id: UUID, fleet: List<Ship>): Boolean

    // Allow an user to define a set of shots on each round.
    fun makeShot(token: UUID, game_id: UUID, shot: Shot): Game

    // Inform the user about the state of its fleet.
    fun getMyFleet(token: UUID, game_id: UUID): List<Ship>

    // Inform the user about the state of the opponent's fleet.
    fun getOpponentFleet(token: UUID, game_id: UUID): List<OpponentShip>

    // Inform the user about the overall state of a game, namely: game phase (layout definition phase, shooting phase, completed phase).
    fun getGame(token: UUID, game_id: UUID): Game
}
