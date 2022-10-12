package domain

import Result
import java.sql.Timestamp
import java.util.UUID

enum class GamePhase { LAYOUT, SHOOTING, COMPLETED }

enum class Player {
    PLAYER1,
    PLAYER2;

    fun opponent() = if (this == PLAYER1) PLAYER2 else PLAYER1
}

typealias FleetLayoutResult = Result<FleetLayoutError, Game>
typealias MakeShotResult = Result<RoundError, Game>

sealed class FleetLayoutError {
    object NotLayoutPhase: FleetLayoutError()
    object AlreadySubmitted: FleetLayoutError()
    // Replace Later with more specific errors
    object INVALID: FleetLayoutError()
}

sealed class RoundError {
    object NotShootingPhase: RoundError()
    object NotYourTurn: RoundError()
    object RepeatedShot: RoundError()
}

sealed class GameResult {
    object LayoutTimeout: GameResult()
    object Forfeit: GameResult()
    // Normal/Expected result
    object FleetSunk: GameResult()
}

data class Game(
    val game_id: UUID,

    val p1: String,
    val p2: String,

    val rules: GameRules = GameRules(),
    val p1_fleet: Set<Ship> = setOf(),
    val p2_fleet: Set<Ship> = setOf(),

    val p1_missed_shots: Set<Shot> = setOf(),
    val p2_missed_shots: Set<Shot> = setOf(),

    val turn: Player = Player.PLAYER1,
    val turn_shots_counter: Int = rules.shots_per_round,
    // val turn: Player = Player.PLAYER1,
    val turn_deadline: Timestamp? = null,

    // Default value only used when game is created
    // IGNORED when GamePhase != LAYOUT
    val layout_phase_deadline: Timestamp? = Timestamp(System.currentTimeMillis() + rules.layout_timeout_s * 1000)
) {
    val winner: Player? =
        if (p1_fleet.isNotEmpty() && p1_fleet.all { it.isDestroyed })
            Player.PLAYER2
        else if (p2_fleet.isNotEmpty() && p2_fleet.all { it.isDestroyed })
            Player.PLAYER1
        else if (turnDeadlineExpired() && turn == Player.PLAYER1)
            Player.PLAYER2
        else if (turnDeadlineExpired() && turn == Player.PLAYER2)
            Player.PLAYER1
        else null

    val phase =
        if (winner != null || turnDeadlineExpired() || layoutPhaseExpired())
            GamePhase.COMPLETED
        else if (p1_fleet.isEmpty() || p2_fleet.isEmpty())
            GamePhase.LAYOUT
        else
            GamePhase.SHOOTING

    val final_result: GameResult? =
        if (winner != null)
            if (p1_fleet.all { it.isDestroyed } || p2_fleet.all { it.isDestroyed })
                // One of the fleets is destroyed
                GameResult.FleetSunk
            else
                // There is a winner but none of the fleets are destroyed
                // (Note: turn timeout is considered forfeit as well)
                GameResult.Forfeit
        else
            if (p1_fleet.isEmpty() || p2_fleet.isEmpty())
                // No winner and one of the fleets are empty
                GameResult.LayoutTimeout
            else
                null

    fun submitFleetLayout(me: Player, ships: Set<Ship>): FleetLayoutResult {
        if (phase != GamePhase.LAYOUT)
            return Result.Failure(FleetLayoutError.NotLayoutPhase)

        if (me == Player.PLAYER1)
            if (p1_fleet.isNotEmpty())
                return Result.Failure(FleetLayoutError.AlreadySubmitted)
        else
            if (p2_fleet.isNotEmpty())
                return Result.Failure(FleetLayoutError.AlreadySubmitted)

        validateFleetLayout(ships, rules.board_dimensions, rules.ships_configurations)
            .apply {
                if (this is Result.Failure) return Result.Failure(this.value)
            }

        return Result.Success(
            if (me == Player.PLAYER1)
                this.copy(p1_fleet = ships)
            else
                this.copy(p2_fleet = ships)
        )
    }

    fun makeShot(me: Player, shot: Shot): MakeShotResult {
        if (phase != GamePhase.SHOOTING) return Result.Failure(RoundError.NotShootingPhase)
        if (turn != me) return Result.Failure(RoundError.NotYourTurn)

        val opponentShips = if (me == Player.PLAYER1) p2_fleet else p1_fleet
        val opponentShipPartsHit = opponentShips.flatMap { ship -> ship.parts.filter { it.isHit } }
        val myMissedShots = (if (me == Player.PLAYER1) p1_missed_shots else p2_missed_shots).toMutableSet()

        if (
            myMissedShots.contains(shot) ||
            opponentShipPartsHit.any { part -> part.position.row == shot.position.row && part.position.col == shot.position.col }
        ) return Result.Failure(RoundError.RepeatedShot)

        var shotHit = false
        val newOpponentShips = opponentShips.map { ship ->
            ship.copy(
                parts = ship.parts.map { part ->
                    if (isShotHit(part, shot))
                    { // Hit
                        shotHit = true
                        part.copy(isHit = true)
                    } else
                    { // Didn't hit
                        part
                    }
                })
        }.toSet()

        if (!shotHit)
            myMissedShots.add(shot)

        val newPlayer1Fleet = if (me == Player.PLAYER1) p1_fleet else newOpponentShips
        val newTurnShotsCounter =
            if (turn_shots_counter - 1 == 0)
                rules.shots_per_round
            else
                turn_shots_counter - 1
        val newPlayer2Fleet = if (me == Player.PLAYER2) p2_fleet else newOpponentShips
        val newPlayer1MissedShots = if (me == Player.PLAYER1) myMissedShots.toSet() else p1_missed_shots
        val newPlayer2MissedShots = if (me == Player.PLAYER2) myMissedShots.toSet() else p2_missed_shots

        return Result.Success(
            this.copy(
                p1_fleet = newPlayer1Fleet,
                p2_fleet = newPlayer2Fleet,
                p1_missed_shots = newPlayer1MissedShots,
                p2_missed_shots = newPlayer2MissedShots,
                turn_shots_counter = newTurnShotsCounter,
                turn = if (newTurnShotsCounter == rules.shots_per_round) me.opponent() else me,
                turn_deadline = calculateNewTurnDeadline()
            )
        )
    }

    // Extra/Auxiliary Functionality

    fun whichPlayer(username: String): Player? =
        when(username) {
            p1 -> Player.PLAYER1
            p2 -> Player.PLAYER2
            else -> null
        }

    private fun isShotHit(part: ShipPart, shot: Shot): Boolean =
        shot.position.row == part.position.row && shot.position.col == part.position.col

    private fun turnDeadlineExpired(): Boolean =
        turn_deadline != null && Timestamp(System.currentTimeMillis()) > turn_deadline

    private fun calculateNewTurnDeadline() =
        if (phase == GamePhase.SHOOTING) {
            Timestamp(System.currentTimeMillis() + rules.shots_timeout_s * 1000)
        } else null

    private fun layoutPhaseExpired(): Boolean =
        layout_phase_deadline != null
        && Timestamp(System.currentTimeMillis()) > layout_phase_deadline
        && (p1_fleet.isEmpty() || p2_fleet.isEmpty())
}


/*
    fun makeShots(me: Player, shots: Set<Shot>): RoundResult {
        if (phase != GamePhase.SHOOTING) return RoundResult.NotShootingPhase
        if (turn != me) return RoundResult.NotYourTurn
        if (shots.size != rules.shots_per_round) return RoundResult.InvalidNumberOfShots

        val opponent = me.opponent()

        val opponentShips = if (me == Player.PLAYER1) p2_fleet else p1_fleet
        val opponentShipPartsHit = opponentShips.flatMap { ship -> ship.parts.filter { it.isHit } }

        val myMissedShots = (if (me == Player.PLAYER1) p1_missed_shots else p2_missed_shots).toMutableSet()

        if (
            !shots.none { shot ->
                myMissedShots.contains(shot) ||
                opponentShipPartsHit.any { part -> part.position.row == shot.position.row && part.position.col == shot.position.col }
            }
        ) return RoundResult.RepeatedShot

        // Calculate opponent ships_configurations after [shots] have been fired
        val shotsHit = mutableSetOf<Shot>()
        val newOpponentShips = opponentShips.map { ship ->
            ship.copy(
                parts = ship.parts.map { part ->
                    val shot: Shot? = isShotHit(part, shots)
                    if (shot == null)
                    { // Didn't hit
                        part
                    } else
                    { // Hit
                        shotsHit.add(shot)
                        part.copy(isHit = true)
                    }
                })
        }.toSet()

        // Calculate my missed shots after getting feedback from [shots]
        val shotsMissed = shots - shotsHit
        myMissedShots += shotsMissed

        val newPlayer1Fleet = if (me == Player.PLAYER1) p1_fleet else newOpponentShips
        val newPlayer2Fleet = if (me == Player.PLAYER2) p2_fleet else newOpponentShips
        val newPlayer1MissedShots = if (me == Player.PLAYER1) myMissedShots.toSet() else p1_missed_shots
        val newPlayer2MissedShots = if (me == Player.PLAYER2) myMissedShots.toSet() else p2_missed_shots

        return RoundResult.Success(
            this.copy(
                p1_fleet = newPlayer1Fleet,
                p2_fleet = newPlayer2Fleet,
                p1_missed_shots = newPlayer1MissedShots,
                p2_missed_shots = newPlayer2MissedShots,
                turn = opponent,
                turn_deadline = calculateNewTurnDeadline()
            )
        )
    }
*/