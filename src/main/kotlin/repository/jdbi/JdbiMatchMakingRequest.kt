package repository.jdbi

import domain.MatchMakingRequest
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import repository.interfaces.MatchMakingRequestRepository

class JdbiMatchMakingRequest (
    private val handle: Handle
): MatchMakingRequestRepository {

    override fun create(matchMakingRequest: MatchMakingRequest): Boolean =
        handle.createUpdate(
            """
               insert into matchmakingrequests
               values
               (:timestamp, :p1, :p2, :mode, :game_id)
            """
        )
            .bind("timestamp", matchMakingRequest.timestamp)
            .bind("p1", matchMakingRequest.p1)
            .bind("p2", matchMakingRequest.p2)
            .bind("mode", matchMakingRequest.mode)
            .bind("game_id", matchMakingRequest.game_id)
            .execute() == 1

    override fun update(matchMakingRequest: MatchMakingRequest): Boolean  =
        handle.createUpdate(
            """
               update matchmakingqequests set
               timestamp = :timestamp, 
               p1 = :p1,
               p2 = :p2, 
               mode = :mode,
               game_id = :game_id
               
               where 
               p1 = :p1 or
               p2 = :p1
            """
        )
            .bind("timestamp", matchMakingRequest.timestamp)
            .bind("p1", matchMakingRequest.p1)
            .bind("p2", matchMakingRequest.p2)
            .bind("mode", matchMakingRequest.mode)
            .bind("game_id", matchMakingRequest.game_id)
            .execute() == 1

    override fun deleteByUsername(username: String): Boolean =
        handle.createUpdate(
            """
                delete from matchmakingqequests where
                p1 = :username or
                p2 = :username
            """
        )

            .bind("username", username)
            .execute() == 1

    override fun getByUsername(username: String): MatchMakingRequest? =
        handle.createQuery(
            """
               select * from matchmakingqequests
               where
               p1 = :username or 
               p2 = :username
            """
        )
            .bind("username", username)
            .mapTo<MatchMakingRequest>()
            .singleOrNull()
}