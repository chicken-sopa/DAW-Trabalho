package services.interfaces

import domain.UserRanking

interface IStatsServices {

    // Obtain statistical and ranking information, such as number of played games and users ranking, by an unauthenticated user.
    fun getGamesPlayedByUsername(username: String): Int

    // Obtain statistical and ranking information, such as number of played games and users ranking, by an unauthenticated user.
    fun getUserGamesWonByUsername(username: String): Int

    // Obtain statistical and ranking information, such as number of played games and users ranking, by an unauthenticated user.
    fun getUserRankingByUsername(username: String): UserRanking

    // Obtain statistical and ranking information, such as number of played games and users ranking, by an unauthenticated user.
    fun getRanking(): List<UserRanking>
}