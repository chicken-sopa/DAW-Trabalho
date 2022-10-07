package repository

import domain.Game
import java.util.*

interface GamesRepository {

    fun create(game: Game): Boolean

    fun getById(game_id: UUID): Game?

    fun update(game: Game): Boolean
}