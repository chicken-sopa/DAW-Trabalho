package repository.jdbi

import domain.SystemInfo
import org.jdbi.v3.core.Handle
import repository.SystemRepository

class JdbiSystemRepository(
    val handle: Handle
): SystemRepository {
    override fun get(): SystemInfo {
        TODO("Not yet implemented")
    }
}