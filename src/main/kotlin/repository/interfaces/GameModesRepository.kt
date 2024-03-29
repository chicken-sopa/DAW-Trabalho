package repository.interfaces

import domain.GameMode


interface GameModesRepository {
    fun getGameModes(): List<GameMode>

    fun getGameModeByName(game_mode: String): GameMode?
}