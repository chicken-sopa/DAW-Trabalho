package repository

import repository.interfaces.GamesRepository
import repository.interfaces.SystemRepository
import repository.interfaces.UsersRepository

interface Transaction {

    val systemRepository: SystemRepository
    val usersRepository: UsersRepository
    val gamesRepository: GamesRepository

    fun rollback()
}

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}