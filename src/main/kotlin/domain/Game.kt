package domain

import java.sql.Timestamp
import java.util.UUID

enum class GamePhase { LAYOUT, SHOOTING, COMPLETED }

enum class Player {
    PLAYER1,
    PLAYER2;

    fun opponent() = if (this == PLAYER1) PLAYER2 else PLAYER1
}

data class Game(
    val game_id: UUID,
    val rules: GameRules = GameRules(),

    val p1: String,
    val p2: String,

    val p1_fleet: Set<Ship> = setOf(),
    val p2_fleet: Set<Ship> = setOf(),

    val p1_missed_shots: Set<Shot> = setOf(),
    val p2_missed_shots: Set<Shot> = setOf(),

    val turn: Player = Player.PLAYER1,
    // Since game starts in LAYOUT phase does not make sense to start the turn_deadline
    // IGNORED when GamePhase != SHOOTING
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

    val game_phase =
        if (winner != null || turnDeadlineExpired() || layoutPhaseExpired())
            GamePhase.COMPLETED
        else if (p1_fleet.isEmpty() || p2_fleet.isEmpty())
            GamePhase.LAYOUT
        else
            GamePhase.SHOOTING

    sealed class FleetLayoutResult {
        object NotLayoutPhase: FleetLayoutResult()
        object AlreadySubmitted: FleetLayoutResult()
        data class Success(val newGame: Game): FleetLayoutResult()
    }

    sealed class RoundResult {
        object NotShootingPhase: RoundResult()
        object NotYourTurn: RoundResult()
        object InvalidNumberOfShots: RoundResult()
        object RepeatedShot: RoundResult()
        data class Success(val newGame: Game): RoundResult()
    }

    sealed class GameCompletionReason {
        object LayoutTimeout: GameCompletionReason()
        data class Forfeit(val winner: Player): GameCompletionReason()
        data class FleetSunk(val winner: Player): GameCompletionReason()
        object Unknown: GameCompletionReason()
    }

    fun submitFleetLayout(me: Player, ships: Set<Ship>): FleetLayoutResult {
        if (game_phase != GamePhase.LAYOUT)
            return FleetLayoutResult.NotLayoutPhase

        if (me == Player.PLAYER1)
            if (p1_fleet.isNotEmpty())
                return FleetLayoutResult.AlreadySubmitted
        else
            if (p2_fleet.isNotEmpty())
                return FleetLayoutResult.AlreadySubmitted

        validateFleetLayout(ships, rules.board_dimensions, rules.ships_configurations)

        // Not throw -> valid fleet layout
        return FleetLayoutResult.Success(
            newGame = if (me == Player.PLAYER1)
                this.copy(p1_fleet = ships)
            else
                this.copy(p2_fleet = ships)
        )
    }

    fun makeShots(me: Player, shots: Set<Shot>): RoundResult {
        if (game_phase != GamePhase.SHOOTING) return RoundResult.NotShootingPhase
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
                    val shot: Shot? = hitShipPart(part, shots)
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

    fun gameCompletionReason(): GameCompletionReason {
        check(game_phase == GamePhase.COMPLETED)
        { "Game is not completed" }

        // "Normal" win
        if (winner != null && (p1_fleet.all { it.isDestroyed } || p2_fleet.all { it.isDestroyed }))
            return GameCompletionReason.FleetSunk(winner)

        // Layout timeout (no winner)
        if (winner == null && (p1_fleet.isEmpty() || p2_fleet.isEmpty()))
            return GameCompletionReason.LayoutTimeout

        // None of the fleets are sunk but there is a winner + NOT (Layout timeout)
        if (winner != null && !p1_fleet.all { it.isDestroyed } && !p2_fleet.all { it.isDestroyed })
            return GameCompletionReason.Forfeit(winner)

        return GameCompletionReason.Unknown
    }

    // Extra/Auxiliary Functionality

    fun whichPlayer(username: String): Player? =
        when(username) {
            p1 -> Player.PLAYER1
            p2 -> Player.PLAYER2
            else -> null
        }

    private fun hitShipPart(part: ShipPart, shot: Set<Shot>): Shot? =
        shot.find { it.position.row == part.position.row && it.position.col == part.position.col }

    private fun turnDeadlineExpired(): Boolean =
        turn_deadline != null && Timestamp(System.currentTimeMillis()) > turn_deadline

    private fun calculateNewTurnDeadline() =
        if (game_phase == GamePhase.SHOOTING) {
            Timestamp(System.currentTimeMillis() + rules.shots_timeout_s * 1000)
        } else null

    private fun layoutPhaseExpired(): Boolean =
        layout_phase_deadline != null
        && Timestamp(System.currentTimeMillis()) > layout_phase_deadline
        && (p1_fleet.isEmpty() || p2_fleet.isEmpty())
}
