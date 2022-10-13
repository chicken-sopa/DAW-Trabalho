package repository.jdbi

import domain.BoardDimensions
import domain.GameMode
import domain.ShipConfiguration
import io.leangen.geantyref.TypeToken
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import repository.GameModesRepository
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
}

data class GameModeDBModel(
    val name: String,
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
                name,
                gson.fromJson(board_dimensions, BoardDimensions::class.java),
                gson.fromJson(ships_configuration, ships_configuration_list_type),
                shots_per_round,
                layout_timeout_s,
                shots_timeout_s
            )
        }

    companion object {
        fun fromGameMode(gameMode: GameMode) =
            GameModeDBModel(
                gameMode.name,
                gameMode.board_dimensions.toJsonString(),
                gameMode.ships_configurations.toJsonString(),
                gameMode.shots_per_round,
                gameMode.layout_timeout_s,
                gameMode.shots_timeout_s
            )
    }
}