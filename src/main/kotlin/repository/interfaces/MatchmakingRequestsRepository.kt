package repository.interfaces

import domain.MatchMakingRequest

interface MatchmakingRequestsRepository {

    fun create(matchMakingRequest: MatchMakingRequest): Boolean

    fun update(matchMakingRequest: MatchMakingRequest): Boolean

    fun deleteByUsername(username: String): Boolean?

    fun getByUsername(username: String): MatchMakingRequest?

}