package services.interfaces

import domain.GameMode
import java.util.*

sealed class JoinMatchmakingResult {
    data class Joint(val game_id: UUID): JoinMatchmakingResult()
    object InQueue: JoinMatchmakingResult()
}

data class MatchmakingState(
    val complete: Boolean,
    val gameModeName: String,
    val gameId: UUID? = null
)

interface IMatchmakingServices {

    /**
     * Join Matchmaking
     *
     * 1. If user is already present in matchmaking table check if game != null
     * if (game != null) check if game.phase == COMPLETED
     * if COMPLETED delete matchmaking entry and go to next step. If not, throw ('You have already joint matchmaking')
     *
     * 2. Check if there is a proper game to join (using matchmaking algorithm) and if not create a
     * matchmaking request which should be fulfilled by a second user later.
     *
     * */
    fun join(username: String, game_mode: GameMode): JoinMatchmakingResult

    /**
     * If able to leave return true
     * If the game has already started return false
     * If not in matchmaking +
     * */
    fun cancelSearch(username: String): Boolean


    fun getState(username: String): MatchmakingState
}