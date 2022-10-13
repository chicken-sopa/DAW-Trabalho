package repository

import domain.Game
import domain.GameMode
import java.util.*

interface GamesRepository {

    fun create(game: Game): Boolean

    fun getById(game_id: UUID): Game?

    fun update(game: Game): Boolean

    fun getGameModes(): List<GameMode>

    fun getGameModeByName(game_mode: String): GameMode?
}