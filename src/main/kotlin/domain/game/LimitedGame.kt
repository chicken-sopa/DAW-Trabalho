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

    val turn: Player,
    val turn_shots_counter: Int,
    val turn_deadline: Timestamp?,

    val layout_phase_deadline: Timestamp?,

    val winner: Player?,
    val phase: GamePhase,
    val result: GameResult?
) {
    companion object  {
        fun fromGame(): LimitedGame {
            TODO()
        }
    }
}