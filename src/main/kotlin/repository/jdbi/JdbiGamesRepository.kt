package repository.jdbi

import domain.*
import repository.GamesRepository
import org.jdbi.v3.core.Handle
import java.sql.Timestamp
import java.util.*

class JdbiGamesRepository(
    private val handle: Handle,
) : GamesRepository {

    override fun create(game: Game): Boolean {
        TODO("Not yet implemented")
    }

    override fun getById(game_id: UUID): Game? {
        TODO("Not yet implemented")
    }

    override fun update(game: Game): Boolean {
        TODO("Not yet implemented")
    }
}

data class GameDbModel(
    val game_id: String,
    val rules: GameRules,

    val player1: String,
    val player2: String,

    val player1_fleet: FleetLayout,
    val player2_fleet: FleetLayout,

    val player1_missed_shots: Set<Shot>,
    val player2_missed_shots: Set<Shot>,

    val turn: Player,

    val layout_phase_deadline: Timestamp?
) {
    fun toGame() = Game(
        game_id, rules,
        player1, player2,
        player1_fleet, player2_fleet,
        player1_missed_shots, player2_missed_shots,
        turn, layout_phase_deadline
    )
}