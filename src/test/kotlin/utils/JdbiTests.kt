package utils

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import repository.Transaction
import repository.TransactionManager
import repository.jdbi.JdbiTransaction
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

/** To be used by tests targeting "Repository" */
fun testWithHandleAndRollback(block: (Handle) -> Unit) =
    jdbi.useTransaction<Exception> { handle ->
        block(handle)
        // handle.commit()
        handle.rollback()
    }

/** To be used by tests targeting "Services" */
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


// REMOVE LATER
fun main() {

}
