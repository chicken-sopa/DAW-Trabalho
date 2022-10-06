package utils

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import repository.Transaction
import repository.TransactionManager
import repository.jdbi.JdbiTransaction
import repository.jdbi.JdbiUsersRepository
import repository.jdbi.configure

private const val CONNECTION_STRING = "jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit"

val jdbi = Jdbi.create(
    PGSimpleDataSource().apply {
        setURL(CONNECTION_STRING)
    }
).configure()

/**
 * NOTE: The transactions do not commit while testing
 * */

fun testWithHandleAndRollback(block: (Handle) -> Unit) =
    jdbi.useTransaction<Exception> { handle ->
        block(handle)
        handle.rollback()
    }

fun testWithTransactionManagerAndRollback(block: (TransactionManager) -> Unit) =
    jdbi.useTransaction<Exception> { handle ->
        val transaction = JdbiTransaction(handle)

        val transactionManager = object : TransactionManager {
            override fun <R> run(block: (Transaction) -> R): R {
                return block(transaction)
            }
        }
        block(transactionManager)

        handle.rollback()
    }


fun main() {
    testWithHandleAndRollback { handle ->
        val usersRepository = JdbiUsersRepository(handle)

        val user = usersRepository.getUserByUsername("Jose Menezes")
        println(user)
    }
}
