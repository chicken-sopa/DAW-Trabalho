package repository.jdbi.utils

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import repository.*
import repository.interfaces.GamesRepository
import repository.interfaces.SystemRepository
import repository.interfaces.UsersRepository
import repository.jdbi.JdbiGamesRepository
import repository.jdbi.JdbiSystemRepository
import repository.jdbi.JdbiUsersRepository

class JdbiTransaction (
    private val handle: Handle
): Transaction {

    override val systemRepository: SystemRepository by lazy { JdbiSystemRepository(handle) }

    override val usersRepository: UsersRepository by lazy { JdbiUsersRepository(handle) }

    override val gamesRepository: GamesRepository by lazy { JdbiGamesRepository(handle) }

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