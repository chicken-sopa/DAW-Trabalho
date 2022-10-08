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

    @Throws(Exception::class)
    fun submitFleetLayout(me: Player, ships: Set<Ship>): Game {
        check(game_phase == GamePhase.LAYOUT)
        { "Can't submit layout when game is not in Layout phase" }

        if (me == Player.PLAYER1)
            check(p1_fleet.isEmpty()) { "You have already submitted a fleet layout" }
        else
            check(p2_fleet.isEmpty()) { "You have already submitted a fleet layout" }

        validateFleetLayout(ships, rules.board_dimensions, rules.ships_configurations)

        // Not throw -> valid fleet layout
        return if (me == Player.PLAYER1)
            this.copy(p1_fleet = ships)
        else
            this.copy(p2_fleet = ships)
    }

    sealed class GameCompletionReason {
        object LayoutTimeout: GameCompletionReason()
        data class Forfeit(val winner: Player): GameCompletionReason()
        data class FleetSunk(val winner: Player): GameCompletionReason()
        object Unknown: GameCompletionReason()
    }

    fun gameCompletionReason(): GameCompletionReason {
        check(game_phase == GamePhase.COMPLETED)
        { "Game is not completed yet" }

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

    fun makeShots(me: Player, shots: Set<Shot>): Game {
        check(game_phase == GamePhase.SHOOTING)
        { "Game not in shooting phase" }
        check(turn == me)
        { "Not your turn" }
        require(shots.size == rules.shots_per_round)
        { "The number of shots needs to be exactly ${rules.shots_per_round}" }

        val opponent = me.opponent()

        val opponentShips = if (me == Player.PLAYER1) p2_fleet else p1_fleet
        val opponentShipPartsHit = opponentShips.flatMap { ship -> ship.parts.filter { it.isHit } }

        val myMissedShots = (if (me == Player.PLAYER1) p1_missed_shots else p2_missed_shots).toMutableSet()

        check(
            shots.none { shot ->
                myMissedShots.contains(shot) ||
                opponentShipPartsHit.any { part -> part.position.row == shot.position.row && part.position.col == shot.position.col }
            }
        ) { "Can't fire a shot at the same place twice" }

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

        val newPlayer1_fleet = if (me == Player.PLAYER1) p1_fleet else newOpponentShips
        val newPlayer2_fleet = if (me == Player.PLAYER2) p2_fleet else newOpponentShips
        val newPlayer1_missed_shots = if (me == Player.PLAYER1) myMissedShots.toSet() else p1_missed_shots
        val newPlayer2_missed_shots = if (me == Player.PLAYER2) myMissedShots.toSet() else p2_missed_shots

        return this.copy(
            p1_fleet = newPlayer1_fleet,
            p2_fleet = newPlayer2_fleet,
            p1_missed_shots = newPlayer1_missed_shots,
            p2_missed_shots = newPlayer2_missed_shots,
            turn = opponent,
            turn_deadline = calculateNewTurnDeadline()
        )
    }

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
