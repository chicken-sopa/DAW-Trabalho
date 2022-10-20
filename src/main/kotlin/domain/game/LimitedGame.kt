package domain.game

import domain.GameMode
import java.sql.Timestamp
import java.util.*

data class LimitedGame(
    val game_id: UUID,
    val mode: GameMode,

    val me: String,
    val opponent: String,

    val my_fleet: Set<Ship>,
    // Limited Information
    val opponent_damaged_fleet: Set<OpponentShip>,

    val my_missed_shots: Set<Shot>,
    val opponent_missed_shots: Set<Shot>,

    val turn: String?,
    val turn_shots_counter: Int,
    val turn_deadline: Timestamp?,

    val layout_phase_deadline: Timestamp?,

    val winner: String?,
    val phase: GamePhase,
    val result: GameResult?
) {
    companion object {
        fun fromGame(me_username: String, game: Game): LimitedGame {
            val me = game.whichPlayer(me_username)

            return LimitedGame(
                game.game_id, game.mode, me_username,
                opponent = if (me == Player.PLAYER1) game.p2 else game.p1,
                my_fleet = if (me == Player.PLAYER1) game.p1_fleet else game.p2_fleet,
                opponent_damaged_fleet =
                    if (me == Player.PLAYER1)
                        game.p2_fleet
                            // Only get ships where we hit at least 1 part
                            .filter { it.parts.count { p -> p.isHit } > 0 }
                            // Build an OpponentShip from a Ship (only showing hit parts)
                            .map { ship ->
                                OpponentShip(
                                    ship.parts.filter { it.isHit },
                                    isDestroyed = ship.isDestroyed
                                )
                        }.toSet()
                    else
                        game.p1_fleet
                            .filter { it.parts.count { p -> p.isHit } > 0 }
                            .map { ship ->
                                OpponentShip(
                                    ship.parts.filter { it.isHit },
                                    isDestroyed = ship.isDestroyed
                                )
                            }.toSet(),
                my_missed_shots = if (me == Player.PLAYER1) game.p1_missed_shots else game.p2_missed_shots,
                opponent_missed_shots = if (me == Player.PLAYER1) game.p2_missed_shots else game.p1_missed_shots,
                turn =
                    when (game.turn) {
                        Player.PLAYER1 -> game.p1
                        Player.PLAYER2 -> game.p2
                        else -> null
                    },
                turn_shots_counter = game.turn_shots_counter,
                turn_deadline = game.turn_deadline,
                layout_phase_deadline = game.layout_phase_deadline,
                winner =
                    when (game.winner) {
                        Player.PLAYER1 -> game.p1
                        Player.PLAYER2 -> game.p2
                        else -> null
                    },
                phase = game.phase,
                result = game.result
            )
        }
    }
}