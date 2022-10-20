package services

import domain.UserRanking
import repository.TransactionManager
import services.interfaces.IStatsServices

class StatsServices(
    private val transactionManager: TransactionManager
): IStatsServices {
    override fun getGamesPlayedByUsername(username: String): Int {
        return transactionManager.run {
            val usersRepo = it.usersRepo
            val user = usersRepo.getByUsername(username)
                ?: throw UserErrors.UserDoesNotExist

            return@run user.games_played
        }
    }

    override fun getUserGamesWonByUsername(username: String): Int {
        return transactionManager.run {
            val usersRepo = it.usersRepo
            val user = usersRepo.getByUsername(username)
                ?: throw UserErrors.UserDoesNotExist

            return@run user.games_won
        }
    }

    override fun getUserRankingByUsername(username: String): UserRanking {
       return transactionManager.run {
           val usersRepo = it.rankingRepository

           return@run usersRepo.getByUser(username)
               ?: throw UserErrors.UserDoesNotExist
       }
    }

    override fun getRanking(): List<UserRanking> {
        return transactionManager.run {
            val usersRepo = it.rankingRepository

            return@run usersRepo.get()
        }
    }

    override fun getRanking(offset: Int, limit: Int): List<UserRanking> {
        return transactionManager.run {
            val usersRepo = it.rankingRepository

            return@run usersRepo.get(offset, limit)
        }
    }

    override fun getUserRanking(username: String): UserRanking {
        return transactionManager.run {
            val usersRepo = it.rankingRepository

            return@run usersRepo.getByUser(username)
                ?: throw UserErrors.UserDoesNotExist
        }
    }
}