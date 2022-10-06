package repository

interface Transaction {

    val usersRepository: UsersRepository
    val gamesRepository: GamesRepository

    fun rollback()
}

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}