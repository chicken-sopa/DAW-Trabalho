package domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GameTests {

    private val TEST_GAME_ID = UUID.randomUUID()

    private val rules = GameRules(
        ships_configurations = listOf(
            ShipConfiguration(1, 3),
            ShipConfiguration(1, 2)
        ),
        shots_per_round = 2
    )
    // For the rules above
    private val validFleet = setOf(
        Ship(
            listOf(
                ShipPart(Position(0, 1)), ShipPart(Position(0, 2)), ShipPart(Position(0, 3))
            )
        ),
        Ship(
            listOf(
                ShipPart(Position(2, 3)), ShipPart(Position(3, 3))
            )
        )
    )

    @Test
    fun `Create game and verify state`() {
        val sutGame = Game(
            TEST_GAME_ID,
            GameRules(),
            "player1",
            "player2"
        )

        assertEquals(sutGame.turn, Player.PLAYER1)
        assertEquals(sutGame.turn_deadline, null)
        assertNotNull(sutGame.layout_phase_deadline)
        assertEquals(sutGame.game_phase, GamePhase.LAYOUT)
        assertNull(sutGame.winner)
    }

    @Test
    fun `Submit valid fleet layout of Player1 to game`() {
        val sutGame = Game(
            TEST_GAME_ID,
            rules,
            "player1",
            "player2"
        )

        assertEquals(sutGame.turn, Player.PLAYER1)
        assertEquals(sutGame.turn_deadline, null)
        assertNotNull(sutGame.layout_phase_deadline)
        assertEquals(sutGame.game_phase, GamePhase.LAYOUT)
        assertNull(sutGame.winner)

        val updatedGame = assertDoesNotThrow {
            sutGame.submitFleetLayout(
                Player.PLAYER1,
                validFleet
            )
        }

        assert(updatedGame is Game.FleetLayoutResult.Success)
        assert((updatedGame as Game.FleetLayoutResult.Success).newGame.p1_fleet.isNotEmpty())
    }

    @Test
    fun `Submit invalid fleet layout of Player1 to game`() {
        val sutGame = Game(
            TEST_GAME_ID,
            rules,
            "player1",
            "player2"
        )

        assertThrows<Exception> {
            sutGame.submitFleetLayout(
                Player.PLAYER1,
                setOf(
                    Ship(
                        listOf(
                            ShipPart(Position(0, 1)), ShipPart(Position(0, 2)), ShipPart(Position(0, 3))
                        )
                    ),
                    Ship(
                        listOf(
                            ShipPart(Position(3, 3))
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `Submit valid fleet layout of Player1 and Player2 to check game phase updated`() {
        val sutGame = Game(
            TEST_GAME_ID,
            rules,
            "player1",
            "player2"
        )

        val gameAfterP1FleetLayout = assertDoesNotThrow {
            (
                sutGame.submitFleetLayout(
                    Player.PLAYER1,
                    validFleet
                ) as Game.FleetLayoutResult.Success
            ).newGame
        }


        val updatedGame = assertDoesNotThrow {
            (
                gameAfterP1FleetLayout
                    .submitFleetLayout(
                    Player.PLAYER2,
                    validFleet
                ) as Game.FleetLayoutResult.Success
            ).newGame
        }

        assert(updatedGame.p1_fleet.isNotEmpty())
        assert(updatedGame.p2_fleet.isNotEmpty())
        assertEquals(updatedGame.game_phase, GamePhase.SHOOTING)
    }

    @Test
    fun `Submit layout twice for same Player`() {
        val sutGame = Game(
            TEST_GAME_ID,
            rules,
            "player1",
            "player2",
        )

        val gameAfterP1FleetLayout = assertDoesNotThrow {
            (
                sutGame.submitFleetLayout(
                    Player.PLAYER1,
                    validFleet
                ) as Game.FleetLayoutResult.Success
            ).newGame
        }

        val fleetLayoutResult = gameAfterP1FleetLayout.submitFleetLayout(
            Player.PLAYER1,
            setOf()
        )

        assert(fleetLayoutResult is Game.FleetLayoutResult.AlreadySubmitted)
    }

    @Test
    fun `Make valid shots swaps turn`() {
        val sutGame = Game(
            TEST_GAME_ID,
            rules,
            "player1",
            "player2",
            validFleet,
            validFleet
        )

        val shots = setOf(Shot(Position(0, 1)), Shot(Position(3, 3)))

        val game1 =
            (
                sutGame.makeShots(
                    Player.PLAYER1,
                    shots
                ) as Game.RoundResult.Success
            ).newGame


        assertEquals(game1.turn, Player.PLAYER2)
        // Should be calculated when makeShots builds a new game
        assertNotNull(game1.turn_deadline)
        assertEquals(
            game1.p2_fleet.flatMap { it.parts }.count { it.isHit },
            shots.size
        )
    }

    @Test
    fun `Make valid shots, some missed`() {
        val sutGame = Game(
            TEST_GAME_ID,
            rules,
            "player1",
            "player2",
            validFleet,
            validFleet,
            setOf(),
            setOf()
        )

        val shots = setOf(Shot(Position(1, 1)), Shot(Position(3, 3)))

        val game1 =
            (
                sutGame.makeShots(
                    Player.PLAYER1,
                    shots
                ) as Game.RoundResult.Success
            ).newGame

        assertEquals(game1.turn, Player.PLAYER2)
        // Should be calculated when makeShots builds a new game
        assertNotNull(game1.turn_deadline)
        assertEquals(
            game1.p2_fleet.flatMap { it.parts }.count { it.isHit },
            1
        )
        assertEquals(game1.p1_missed_shots.size, 1)
        assert(game1.p1_missed_shots.contains(Shot(Position(1, 1))))
    }
}