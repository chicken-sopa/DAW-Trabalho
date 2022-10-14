package repository.interfaces

import domain.game.Game
import java.util.*

interface GamesRepository {

    fun create(game: Game): Boolean

    fun getById(game_id: UUID): Game?

    fun update(game: Game): Boolean
}