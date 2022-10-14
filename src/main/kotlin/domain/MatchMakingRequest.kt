package domain

import java.sql.Timestamp
import java.util.*

data class MatchMakingRequest (
    val searching_time: Timestamp = Timestamp(System.currentTimeMillis()),
    val p1: String,
    val p2: String? = null,
    val mode: String,
    val game_id: UUID? = null
)
