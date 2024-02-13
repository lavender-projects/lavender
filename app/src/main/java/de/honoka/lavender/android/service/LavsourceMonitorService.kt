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
    }

    override val companion: BaseServiceCompanion = Companion

    private val thread = Thread {
        var lavsourceInfoList: List<LavsourceInfo> = LavsourceInfoDao.listEnabled()
        while(true) {
            if(Thread.currentThread().isInterrupted) return@Thread
            if(lavsourceInfoList.isEmpty()) {
                lavsourceInfoList = LavsourceInfoDao.listEnabled()
            }
            lavsourceInfoList.forEach {
                LavsourceUtils.getLavsourceBaseUrl(it.packageName!!)?.let { url ->
                    baseUrlMap[it.id!!] = url
                }
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