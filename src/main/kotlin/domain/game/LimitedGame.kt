package domain.game

import java.sql.Timestamp
import java.util.*

data class LimitedGame(
    val game_id: UUID,
    val mode: GameMode,

    val p1: String,
    val p2: String,

    val p1_fleet: Set<Ship> = setOf(),
    // Limited Information
    val p2_fleet: Set<OpponentShip> = setOf(),

    val p1_missed_shots: Set<Shot> = setOf(),
    val p2_missed_shots: Set<Shot> = setOf(),

    val turn: Player = Player.PLAYER1,
    val turn_shots_counter: Int = mode.shots_per_round,
    val turn_deadline: Timestamp? = null,

    val layout_phase_deadline: Timestamp? = Timestamp(System.currentTimeMillis() + mode.layout_timeout_s * 1000)
)