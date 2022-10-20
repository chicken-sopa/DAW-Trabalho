package repository.interfaces

import domain.UserRanking

interface RankingRepository {

    fun get(): List<UserRanking>

    fun get(offset: Int, limit: Int): List<UserRanking>

    fun getByUser(username: String): UserRanking?

}