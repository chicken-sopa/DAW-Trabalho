package services.interfaces

import domain.SystemInfo

interface ISystemServices {

    // Obtain information about the system, such as the system authors and the system version, by an unauthenticated user.
    fun getSystemInfo(): SystemInfo

}