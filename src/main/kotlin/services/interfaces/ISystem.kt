package services.interfaces

import domain.SystemInfo

interface ISystem {

    // Obtain information about the system, such as the system authors and the system version, by an unauthenticated user.
    fun getSystemInfo(): SystemInfo

}