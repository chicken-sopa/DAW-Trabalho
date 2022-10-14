package repository.interfaces

import domain.SystemInfo

interface SystemRepository {

    fun get(): SystemInfo

}