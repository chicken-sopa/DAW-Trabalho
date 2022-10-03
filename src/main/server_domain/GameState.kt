package com.example.first_app.server_domain

import com.example.first_app.domain.OpponentShip
import java.sql.Timestamp

enum class GamePhase { LAYOUT, SHOOTING, COMPLETED }

fun main() {
    println("HI")
}

enum class Player {
    PLAYER1,
    PLAYER2;

    fun opponent() = if (this == PLAYER1) PLAYER2 else PLAYER1
}

data class Remote_GameState(
    val game_id: String,

    val player1_name: String,
    val player2_name: String,

    val player1_ships: List<Ship> = listOf(),
    val player1_missed_shots: List<Shot> = listOf(),

    val player2_ships: List<Ship> = listOf(),
    val player2_missed_shots: List<Shot> = listOf(),

    val turn: Player = Player.PLAYER1,
    val turn_deadline: Timestamp? = null,

    val game_phase: GamePhase = GamePhase.LAYOUT,
    val winner: Player? = null,
)

/**
 * - makeShot(player: Player, shot: Shot)
 * - toResponseGameState(player: Player)
 * */
data class Global_GameState(
    val game_id: String,

    val players: Map<String, Player>,

    val ships: Map<Player, List<Ship>> = mapOf(
        Player.PLAYER1 to listOf(),
        Player.PLAYER2 to listOf()
    ),
    val missed_shots: Map<Player, List<Shot>> = mapOf(
        Player.PLAYER1 to listOf(),
        Player.PLAYER2 to listOf()
    ),

    val turn: Player = Player.PLAYER1,
    val turn_deadline: Timestamp? = null,

    val game_phase: GamePhase = GamePhase.LAYOUT,
    val winner: Player? = null,
) {
    fun makeShot(player: Player, shot: Shot): Global_GameState {
        require(game_phase == GamePhase.SHOOTING)

        val opponent = player.opponent()
        // To be able to swap opponent ships if needed
        val newShips = ships.toMutableMap()
        // To be able to register new missed shot if needed
        val missedShots = missed_shots.toMutableMap()
        val myMissedShots = missedShots[player]!!.toMutableList()

        var hasHit = false
        newShips[opponent] = ships[opponent]!!.map { ship ->
            ship.copy(
                parts = ship.parts.map { part ->
                    if (isHit(shot, part)) {
                        hasHit = true
                        part.copy(isHit = true)
                    } else part
            })
        }

        if (!hasHit) {
            missedShots[player] = myMissedShots + shot
        }

        return this.copy(
            ships=newShips,
            missed_shots=missedShots,
            turn=if (hasHit) player else opponent,
            turn_deadline=calculateTurnDeadline(),
            winner=calculateWinner()
        )
    }

    private fun isHit(shot: Shot, shipPart: ShipPart) = shot.row == shipPart.row && shot.col == shipPart.col

    /**  */
    private fun calculateWinner(): Player? {
        TODO()
    }

    /**  */
    private fun calculateTurnDeadline(): Timestamp {
    TODO()
    }
}

data class Response_GameState(
    val game_id: String,

    val player1_name: String,
    val player2_name: String,

    // Information about THE PLAYER who made the request
    val myShips: List<Ship> = listOf(),
    val opponentMissedShots: List<Shot> = listOf(),
    // Information about the OPPONENT of the player who made the request
    val opponentShips: List<OpponentShip> = listOf(),
    val myMissedShots: List<Shot> = listOf(),

    val turn: Player = Player.PLAYER1,
    // NULL while the game does not begin
    val turn_deadline: Timestamp? = null,

    val game_phase: GamePhase = GamePhase.LAYOUT,
    val winner: Player? = null,
)

data class User(val username: String, val password: String, val ranking_points: Int = 0)
