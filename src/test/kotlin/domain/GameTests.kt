package domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import ActionResult

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
            "player1",
            "player2"
        )

        assertEquals(sutGame.turn, Player.PLAYER1)
        assertEquals(sutGame.turn_deadline, null)
        assertNotNull(sutGame.layout_phase_deadline)
        assertEquals(sutGame.phase, GamePhase.LAYOUT)
        assertNull(sutGame.winner)
    }

    @Test
    fun `Submit valid fleet layout of Player1 to game`() {
        val sutGame = Game(
            TEST_GAME_ID,
            "player1",
            "player2",
            rules
        )

        assertEquals(sutGame.turn, Player.PLAYER1)
        assertEquals(sutGame.turn_deadline, null)
        assertNotNull(sutGame.layout_phase_deadline)
        assertEquals(sutGame.phase, GamePhase.LAYOUT)
        assertNull(sutGame.winner)

        val fleetLayoutResult = sutGame.submitFleetLayout(
            Player.PLAYER1,
            validFleet
        )

        assert(fleetLayoutResult is ActionResult.Success)
        assert((fleetLayoutResult as ActionResult.Success).value.p1_fleet.isNotEmpty())
    }

    @Test
    fun `Submit invalid fleet layout of Player1 to game`() {
        val sutGame = Game(
            TEST_GAME_ID,
            "player1",
            "player2",
            rules
        )

        val fleetLayoutResult = sutGame.submitFleetLayout(
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

        assert(fleetLayoutResult is ActionResult.Failure)
        assert((fleetLayoutResult as ActionResult.Failure).value is FleetLayoutError.INVALID)
    }

    @Test
    fun `Submit valid fleet layout of Player1 and Player2 to check game phase updated`() {
        val sutGame = Game(
            TEST_GAME_ID,
            "player1",
            "player2",
            rules
        )

        val gameAfterP1FleetLayout = assertDoesNotThrow {
            (
                sutGame.submitFleetLayout(
                    Player.PLAYER1,
                    validFleet
                ) as ActionResult.Success
            ).value
        }

        val updatedGame = assertDoesNotThrow {
            (
                gameAfterP1FleetLayout
                    .submitFleetLayout(
                        Player.PLAYER2,
                        validFleet
                    ) as ActionResult.Success
            ).value
        }

        assert(updatedGame.p1_fleet.isNotEmpty())
        assert(updatedGame.p2_fleet.isNotEmpty())
        assertEquals(updatedGame.phase, GamePhase.SHOOTING)
    }

    @Test
    fun `Submit layout twice for same Player`() {
        val sutGame = Game(
            TEST_GAME_ID,
            "player1",
            "player2",
            rules
        )

        val gameAfterP1FleetLayout = assertDoesNotThrow {
            (
                sutGame.submitFleetLayout(
                    Player.PLAYER1,
                    validFleet
                ) as ActionResult.Success
            ).value
        }

        val fleetLayoutResult = gameAfterP1FleetLayout.submitFleetLayout(
            Player.PLAYER1,
            setOf()
        )

        assert(fleetLayoutResult is ActionResult.Failure)
        assert((fleetLayoutResult as ActionResult.Failure).value is FleetLayoutError.AlreadySubmitted)
    }

    @Test
    fun `Make valid shots swaps turn`() {
        val sutGame = Game(
            TEST_GAME_ID,
            "player1",
            "player2",
            rules,
            validFleet,
            validFleet
        )

        val shots = setOf(Shot(Position(0, 1)), Shot(Position(3, 3)))

        var updatedGame = sutGame
        for (shot in shots) {
            val makeShotResult = updatedGame.makeShot(Player.PLAYER1, shot)
            assert(makeShotResult is ActionResult.Success)
            updatedGame = (makeShotResult as ActionResult.Success).value
        }

        assertEquals(Player.PLAYER2, updatedGame.turn)
        // Should be calculated when makeShots builds a new game
        assertNotNull(updatedGame.turn_deadline)
        assertEquals(
            updatedGame.p2_fleet.flatMap { it.parts }.count { it.isHit },
            shots.size
        )
    }

    @Test
    fun `Make valid shots, some missed`() {
        val sutGame = Game(
            TEST_GAME_ID,
            "player1",
            "player2",
            rules,
            validFleet,
            validFleet,
            setOf(),
            setOf()
        )

        val shots = setOf(Shot(Position(1, 1)), Shot(Position(3, 3)))

        var updatedGame = sutGame
        for (shot in shots) {
            val makeShotResult = updatedGame.makeShot(Player.PLAYER1, shot)
            assert(makeShotResult is ActionResult.Success)
            updatedGame = (makeShotResult as ActionResult.Success).value
        }

        assertEquals(updatedGame.turn, Player.PLAYER2)
        // Should be calculated when makeShots builds a new game
        assertNotNull(updatedGame.turn_deadline)
        assertEquals(
            updatedGame.p2_fleet.flatMap { it.parts }.count { it.isHit },
            1
        )
        assertEquals(1, updatedGame.p1_missed_shots.size)
        assert(updatedGame.p1_missed_shots.contains(Shot(Position(1, 1))))
    }
}