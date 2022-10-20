package repository

import repository.interfaces.*

interface Transaction {

    val systemRepository: SystemRepository
    val usersRepo: UsersRepository
    val gamesRepository: GamesRepository
    val gameModesRepository: GameModesRepository
    val matchmakingRequestsRepository: MatchmakingRequestsRepository
    val rankingRepository: RankingRepository

    fun rollback()
}

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}