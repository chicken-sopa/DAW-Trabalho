package repository

import domain.GameMode

interface GameModesRepository {
    fun getGameModes(): List<GameMode>
}