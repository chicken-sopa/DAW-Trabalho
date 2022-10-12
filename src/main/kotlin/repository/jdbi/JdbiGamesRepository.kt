package repository.jdbi

import domain.*
import repository.GamesRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import repository.jdbi.utils.gson
import repository.jdbi.utils.toJsonString
import java.sql.Timestamp
import java.util.*
import io.leangen.geantyref.TypeToken

class JdbiGamesRepository(
    private val handle: Handle,
) : GamesRepository {

    override fun create(game: Game): Boolean =
        GameDbModel.fromGame(game).let { newGame ->
            handle.createUpdate(
                """
               insert into games
               (
                    game_id, p1, p2, p1_fleet, p2_fleet, p1_missed_shots, p2_missed_shots, 
                    turn, turn_shots_counter, turn_deadline, layout_phase_deadline, 
                    board_dimensions, ships_configuration, shots_per_round, layout_timeout_s, shot_timeout_s
               ) values
               (
                    :game_id, :p1, :p2, :p1_fleet, :p2_fleet, :p1_missed_shots, :p2_missed_shots, 
                    :turn, :turn_shots_counter, :turn_deadline, :layout_phase_deadline, 
                    :board_dimensions, :ships_configuration, :shots_per_round, :layout_timeout_s, :shot_timeout_s
               )
            """
            )
                .bind("game_id", newGame.game_id)
                .bind("p1", newGame.p1)
                .bind("p2", newGame.p2)
                .bind("p1_fleet", newGame.p1_fleet)
                .bind("p2_fleet", newGame.p2_fleet)
                .bind("p1_missed_shots", newGame.p1_missed_shots)
                .bind("p2_missed_shots", newGame.p2_missed_shots)
                .bind("turn", newGame.turn)
                .bind("turn_shots_counter", newGame.turn_shots_counter)
                .bind("turn_deadline", newGame.turn_deadline)
                .bind("layout_phase_deadline", newGame.layout_phase_deadline)
                .bind("board_dimensions", newGame.board_dimensions)
                .bind("ships_configuration", newGame.ships_configuration)
                .bind("shots_per_round", newGame.shots_per_round)
                .bind("layout_timeout_s", newGame.layout_timeout_s)
                .bind("shot_timeout_s", newGame.shot_timeout_s)
                .execute() == 1
        }

    override fun getById(game_id: UUID): Game? =
        handle.createQuery(
            """
               select * from games where
               game_id = :game_id
            """
        )
            .bind("game_id", game_id)
            .mapTo<GameDbModel>()
            .singleOrNull()
            ?.toGame()

    // NOTE: Can't update game rules
    override fun update(game: Game): Boolean =
        handle.createUpdate(
            """
               update games set
                p1 = :p1, p2 = :p2, 
                p1_fleet = :p1_fleet, p2_fleet = :p2_fleet, 
                p1_missed_shots = :p1_missed_shots, p2_missed_shots = :p2_missed_shots, 
                turn = :turn, turn_shots_counter = :turn_shots_counter
                turn_deadline = :turn_deadline, layout_phase_deadline = :layout_phase_deadline, 
                
                where game_id = :game_id
            """
        )
            .bind("game_id", game.game_id)
            .bind("p1", game.p1)
            .bind("p2", game.p2)
            .bind("p1_fleet", game.p1_fleet)
            .bind("p2_fleet", game.p2_fleet)
            .bind("p1_missed_shots", game.p1_missed_shots)
            .bind("p2_missed_shots", game.p2_missed_shots)
            .bind("turn", game.turn)
            .bind("turn_shots_counter", game.turn_shots_counter)
            .bind("turn_deadline", game.turn_deadline)
            .bind("layout_phase_deadline", game.layout_phase_deadline)
            .execute() == 1
}

data class GameDbModel(
    val game_id: UUID,

    val p1: String,
    val p2: String,

    val p1_fleet: String,
    val p2_fleet: String,

    val p1_missed_shots: String,
    val p2_missed_shots: String,

    val turn: Player,
    val turn_shots_counter: Int,
    val turn_deadline: Timestamp?,
    val layout_phase_deadline: Timestamp?,

    // Game Rules Information
    val board_dimensions: String,
    val ships_configuration: String,

    val shots_per_round: Int,
    val layout_timeout_s: Int,
    val shot_timeout_s: Int
) {
    fun toGame(): Game {
        val ships_configuration_list_type = object : TypeToken<List<ShipConfiguration>>() {}.type
        val shots_set_type = object : TypeToken<Set<Shot>>() {}.type
        val ships_set_type = object : TypeToken<Set<Ship>>() {}.type

        return gson().let { gson ->
            Game(
                game_id,
                p1,
                p2,
                GameRules(
                    board_dimensions = gson.fromJson(board_dimensions, BoardDimensions::class.java),
                    ships_configurations = gson.fromJson(ships_configuration, ships_configuration_list_type),
                    shots_per_round,
                    layout_timeout_s,
                    shot_timeout_s
                ),
                p1_fleet = gson.fromJson(p1_fleet, ships_set_type),
                p2_fleet = gson.fromJson(p2_fleet, ships_set_type),
                p1_missed_shots = gson.fromJson(p1_missed_shots, shots_set_type),
                p2_missed_shots = gson.fromJson(p2_missed_shots, shots_set_type),
                turn,
                turn_shots_counter,
                turn_deadline,
                layout_phase_deadline
            )
        }
    }

    companion object {
        fun fromGame(game: Game): GameDbModel =
            GameDbModel(
                game.game_id,
                game.p1,
                game.p2,
                game.p1_fleet.toJsonString(),
                game.p2_fleet.toJsonString(),
                game.p1_missed_shots.toJsonString(),
                game.p2_missed_shots.toJsonString(),
                game.turn,
                game.turn_shots_counter,
                game.turn_deadline,
                game.layout_phase_deadline,

                game.rules.board_dimensions.toJsonString(),
                game.rules.ships_configurations.toJsonString(),
                game.rules.shots_per_round,
                game.rules.layout_timeout_s,
                game.rules.shots_timeout_s
            )
    }
}
