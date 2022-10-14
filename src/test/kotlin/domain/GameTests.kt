package domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import Result
import domain.game.*
import utils.testGameMode

class GameTests {

    private val TEST_GAME_ID = UUID.randomUUID()

    // For the testGameMode above
    private val validFleet = setOf(
        Ship(
            listOf(
                ShipPart(Position(0, 1)), ShipPart(Position(0, 2)), ShipPart(Position(0, 3))
            )
        ),
        Ship(
            listOf(
                ShipPart(Position(3, 3)), ShipPart(Position(3, 4))
            )
        )
    )

    @Test
    fun `Create game and verify state`() {
        val sutGame = Game(
            TEST_GAME_ID,
            testGameMode,
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
            testGameMode,
            "player1",
            "player2",
        )

        assertEquals(sutGame.turn, Player.PLAYER1)
        assertEquals(sutGame.turn_deadline, null)
        assertNotNull(sutGame.layout_phase_deadline)
        assertEquals(sutGame.phase, GamePhase.LAYOUT)
        assertNull(sutGame.winner)

        val fleetLayoutResult = sutGame.submitFleet(
            Player.PLAYER1,
            validFleet
        )

        println(fleetLayoutResult)

        assert(fleetLayoutResult is Result.Success)
        assert((fleetLayoutResult as Result.Success).value.p1_fleet.isNotEmpty())
    }

    @Test
    fun `Submit invalid fleet layout of Player1 to game`() {
        val sutGame = Game(
            TEST_GAME_ID,
            testGameMode,
            "player1",
            "player2",
        )

        val fleetLayoutResult = sutGame.submitFleet(
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

        assert(fleetLayoutResult is Result.Failure)
        assert((fleetLayoutResult as Result.Failure).value is FleetError.InvalidShipsConfiguration)
    }

    @Test
    fun `Submit valid fleet layout of Player1 and Player2 to check game phase updated`() {
        val sutGame = Game(
            TEST_GAME_ID,
            testGameMode,
            "player1",
            "player2",
        )

        val gameAfterP1FleetLayout = assertDoesNotThrow {
            (
                sutGame.submitFleet(
                    Player.PLAYER1,
                    validFleet
                ) as Result.Success
            ).value
        }

        val updatedGame = assertDoesNotThrow {
            (
                gameAfterP1FleetLayout
                    .submitFleet(
                        Player.PLAYER2,
                        validFleet
                    ) as Result.Success
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
            testGameMode,
            "player1",
            "player2",
        )

        val gameAfterP1FleetLayout = assertDoesNotThrow {
            (
                sutGame.submitFleet(
                    Player.PLAYER1,
                    validFleet
                ) as Result.Success
            ).value
        }

        val fleetLayoutResult = gameAfterP1FleetLayout.submitFleet(
            Player.PLAYER1,
            setOf()
        )

        assert(fleetLayoutResult is Result.Failure)
        assert((fleetLayoutResult as Result.Failure).value is FleetError.AlreadySubmitted)
    }

    @Test
    fun `Make valid shots swaps turn`() {
        val sutGame = Game(
            TEST_GAME_ID,
            testGameMode,
            "player1",
            "player2",
            validFleet,
            validFleet
        )

        val shots = setOf(Shot(Position(0, 1)), Shot(Position(3, 3)))

        var updatedGame = sutGame
        for (shot in shots) {
            val makeShotResult = updatedGame.makeShot(Player.PLAYER1, shot)
            assert(makeShotResult is Result.Success)
            updatedGame = (makeShotResult as Result.Success).value
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
            testGameMode,
            "player1",
            "player2",
            validFleet,
            validFleet,
            setOf(),
            setOf()
        )

        val shots = setOf(Shot(Position(1, 1)), Shot(Position(3, 3)))

        var updatedGame = sutGame
        for (shot in shots) {
            val makeShotResult = updatedGame.makeShot(Player.PLAYER1, shot)
            assert(makeShotResult is Result.Success)
            updatedGame = (makeShotResult as Result.Success).value
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