package repository.jdbi

import domain.UserRanking
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import repository.interfaces.RankingRepository

class JdbiRankingRepository(
    private val handle: Handle
): RankingRepository {

    private val rankingListMaxLimit = 100
    private val rankingListMaxOffset = 100

    override fun get(): List<UserRanking> =
        handle.createQuery(
            """
               select * from ranking
        
               limit $rankingListMaxLimit
            """
        )
            .mapTo<UserRanking>()
            .toList()

    override fun get(offset: Int, limit: Int): List<UserRanking> {
        val actualOffset =
            when {
                offset < 0 -> 0
                offset > rankingListMaxOffset -> rankingListMaxLimit
                else -> offset
            }

        val actualLimit =
            when {
                limit < 0 -> 1
                limit > rankingListMaxLimit -> rankingListMaxLimit
                else -> limit
            }

        return handle.createQuery(
            """
                select * from ranking
        
                offset $actualOffset
                limit $actualLimit
            """
        )
            .mapTo<UserRanking>()
            .toList()
    }

    override fun getByUser(username: String): UserRanking? {
        return handle.createQuery(
            """
                select * from ranking
                where username = :username
            """
        )
            .bind("username", username)
            .mapTo<UserRanking>()
            .singleOrNull()
    }
}