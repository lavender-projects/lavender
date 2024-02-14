package de.honoka.lavender.android.service

import de.honoka.lavender.android.dao.LavsourceInfoDao
import de.honoka.lavender.android.entity.LavsourceInfo
import de.honoka.lavender.android.util.LavsourceUtils
import de.honoka.sdk.util.android.common.BaseService
import de.honoka.sdk.util.android.common.BaseServiceCompanion
import java.util.concurrent.TimeUnit

class LavsourceMonitorService : BaseService() {

    companion object : BaseServiceCompanion() {

        override val serviceClass: Class<out BaseService> = LavsourceMonitorService::class.java

        val baseUrlMap = HashMap<String, String>()

        @Synchronized
        fun syncBaseUrlMap() {
            val lavsourceInfoList: List<LavsourceInfo> = LavsourceInfoDao.listEnabled()
            val idSet = lavsourceInfoList.map { it.id!! }.toSet()
            baseUrlMap.iterator().run {
                while(hasNext()) {
                    val key = next().key
                    if(!idSet.contains(key)) remove()
                }
            }
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