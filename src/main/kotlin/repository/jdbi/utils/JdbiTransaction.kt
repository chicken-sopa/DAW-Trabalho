package repository.jdbi.utils

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import repository.*
import repository.interfaces.*
import repository.jdbi.*

class JdbiTransaction (
    private val handle: Handle
): Transaction {

    override val systemRepository: SystemRepository by lazy {
        JdbiSystemRepository(handle)
    }
    override val usersRepo: UsersRepository by lazy {
        JdbiUsersRepository(handle)
    }
    override val gamesRepository: GamesRepository by lazy {
        JdbiGamesRepository(handle)
    }
    override val gameModesRepository: GameModesRepository by lazy {
        JdbiGameModesRepository(handle)
    }
    override val matchmakingRequestsRepository: MatchmakingRequestsRepository by lazy {
        JdbiMatchmakingRequests(handle)
    }

    override val rankingRepository: RankingRepository by lazy {
        JdbiRankingRepository(handle)
    }

    override fun rollback() {
        handle.rollback()
    }
}

class JdbiTransactionManager(
    private val jdbi: Jdbi
) : TransactionManager {

    override fun <R> run(block: (Transaction) -> R): R =
        jdbi.inTransaction<R, Exception> { handle ->
            val transaction = JdbiTransaction(handle)
            block(transaction)
        }
}