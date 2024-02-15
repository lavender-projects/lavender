package de.honoka.lavender.android.service

import de.honoka.lavender.android.dao.LavsourceInfoDao
import de.honoka.lavender.android.util.LavsourceUtils
import de.honoka.sdk.util.android.common.BaseService
import de.honoka.sdk.util.android.common.BaseServiceCompanion
import de.honoka.sdk.util.kotlin.code.removeIf
import java.util.concurrent.TimeUnit

class LavsourceMonitorService : BaseService() {

    companion object : BaseServiceCompanion() {

        override val serviceClass: Class<out BaseService> = LavsourceMonitorService::class.java

        val baseUrlMap = HashMap<String, String>()

        @Synchronized
        fun syncBaseUrlMap() {
            val lavsourceInfoList = LavsourceInfoDao.listEnabled()
            val idSet = lavsourceInfoList.map { it.id!! }.toSet()
            baseUrlMap.removeIf { !idSet.contains(it.key) }
            lavsourceInfoList.forEach {
                LavsourceUtils.getLavsourceBaseUrl(it.packageName!!)?.let { url ->
                    baseUrlMap[it.id!!] = url
                    return@forEach
                }
                baseUrlMap.remove(it.id!!)
            }
        }
    }

    override val companion: BaseServiceCompanion = Companion

    private val thread = Thread {
        while(true) {
            if(Thread.currentThread().isInterrupted) return@Thread
            runCatching {
                syncBaseUrlMap()
            }
            TimeUnit.SECONDS.sleep(10)
        }
    }

    override fun onServiceCreate() {
        thread.start()
    }

    override fun onServiceDestory() {
        thread.interrupt()
    }
}