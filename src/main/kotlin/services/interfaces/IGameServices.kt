package services.interfaces

import domain.GameMode
import domain.game.*
import java.util.*

interface IGameServices {

    // Allow an user to define the layout of their fleet in the grid.
    fun submitFleet(username: String, game_id: UUID, fleet: List<Ship>): Boolean

    // Allow an user to define a set of shots on each round.
    fun makeShot(username: String, game_id: UUID, shot: Shot): Boolean

    // Inform the user about the state of its fleet.
    fun getMyFleet(username: String, game_id: UUID): List<Ship>

    // Inform the user about the state of the opponent's fleet.
    fun getOpponentFleet(username: String, game_id: UUID): List<OpponentShip>

    // Inform the user about the overall state of a game, namely: game phase (layout definition phase, shooting phase, completed phase).
    fun get(username: String, game_id: UUID): LimitedGame

    /**
     * Forfeit from Game
     * returns
     *  true if could forfeit
     *  false if could not forfeit (Ex: game already over)
     * */
    fun forfeit(username: String, game_id: UUID): Boolean

    fun getGameModes(): List<GameMode>
}
