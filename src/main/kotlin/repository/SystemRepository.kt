package repository

import domain.SystemInfo

interface SystemRepository {

    fun get(): SystemInfo

}