package repository.jdbi

import domain.game.BoardDimensions
import domain.game.GameMode
import domain.game.ShipConfiguration
import io.leangen.geantyref.TypeToken
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import repository.interfaces.GameModesRepository
import repository.jdbi.utils.gson
import repository.jdbi.utils.toJsonString

class JdbiGameModesRepository(
    private val handle: Handle
): GameModesRepository {
    override fun getGameModes(): List<GameMode> =
        handle.createQuery(
            """
               select * from gamemodes 
            """
        )
            .mapTo<GameModeDBModel>()
            .map { it.toGameMode() }
            .toList()

    override fun getGameModeByName(game_mode: String): GameMode? =
        handle.createQuery(
            """
               select * from gamemodes
               where mode_name = :mode_name
            """
        )
            .bind("mode_name", game_mode)
            .mapTo<GameModeDBModel>()
            .singleOrNull()
            ?.toGameMode()
}

data class GameModeDBModel(
    val mode_name: String,
    val board_dimensions: String,
    val ships_configuration: String,
    val shots_per_round: Int,
    val layout_timeout_s: Int,
    val shots_timeout_s: Int
) {
    fun toGameMode(): GameMode =
        gson().let { gson ->
            val ships_configuration_list_type = object : TypeToken<List<ShipConfiguration>>() {}.type

            GameMode(
                mode_name,
                gson.fromJson(board_dimensions, BoardDimensions::class.java),
                gson.fromJson(ships_configuration, ships_configuration_list_type),
                shots_per_round,
                layout_timeout_s,
                shots_timeout_s
            )
        }

    companion object {
        fun fromGameMode(gameMode: GameMode): GameModeDBModel =
            GameModeDBModel(
                gameMode.mode_name,
                gameMode.board_dimensions.toJsonString(),
                gameMode.ships_configurations.toJsonString(),
                gameMode.shots_per_round,
                gameMode.layout_timeout_s,
                gameMode.shots_timeout_s
            )
    }
}