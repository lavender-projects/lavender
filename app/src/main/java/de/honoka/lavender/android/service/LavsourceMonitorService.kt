package de.honoka.lavender.android.service

import de.honoka.lavender.android.dao.LavsourceInfoDao
import de.honoka.lavender.android.util.LavsourceUtils
import de.honoka.sdk.util.android.common.BaseService
import de.honoka.sdk.util.android.common.BaseServiceCompanion
import java.util.concurrent.TimeUnit

class LavsourceMonitorService : BaseService() {

    companion object : BaseServiceCompanion() {

        override val serviceClass: Class<out BaseService> = LavsourceMonitorService::class.java
    }

    override val companion: BaseServiceCompanion = Companion

    private val thread = Thread {
        while(true) {
            if(Thread.currentThread().isInterrupted) return@Thread
            runCatching {
                doMonitor()
            }
            try {
                TimeUnit.SECONDS.sleep(5)
            } catch(t: Throwable) {
                return@Thread
            }
        }
    }

    override fun onServiceCreate() {
        thread.start()
    }

    override fun onServiceDestory() {
        thread.interrupt()
    }

    @Synchronized
    private fun doMonitor() = LavsourceInfoDao.listEnabled().forEach {
        LavsourceUtils.getLavsourceStatus(it.packageName!!)
    }
}