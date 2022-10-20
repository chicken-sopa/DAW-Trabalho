package services

import domain.SystemInfo
import repository.TransactionManager
import services.interfaces.ISystemServices

class SystemServices(
    private val transactionManager: TransactionManager
): ISystemServices {
    override fun getSystemInfo(): SystemInfo {
        return transactionManager.run {
            val systemRepo = it.systemRepository

            return@run systemRepo.get()
        }
    }
}