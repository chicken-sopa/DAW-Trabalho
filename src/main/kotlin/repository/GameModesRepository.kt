package repository

import domain.game.GameMode

interface GameModesRepository {
    fun getGameModes(): List<GameMode>

    fun getGameModeByName(game_mode: String): GameMode?
}