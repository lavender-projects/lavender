package de.honoka.lavender.android.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.honoka.lavender.android.R
import de.honoka.lavender.android.dao.LavsourceInfoDao
import de.honoka.lavender.android.service.LavsourceMonitorService
import de.honoka.lavender.android.util.DatabaseUtils
import de.honoka.lavender.android.util.LavsourceUtils
import de.honoka.lavender.android.util.RecommendedVideoPool
import de.honoka.sdk.util.android.common.GlobalComponents
import de.honoka.sdk.util.android.common.launchCoroutineOnIoThread
import de.honoka.sdk.util.android.server.HttpServer
import de.honoka.sdk.util.android.ui.fullScreenToShow
import de.honoka.sdk.util.android.ui.jumpToWebActivty
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreenToShow()
        setContentView(R.layout.activity_main)
        GlobalComponents.application = application
        launchCoroutineOnIoThread {
            //init可能是一个耗时的操作，故在IO线程中执行，防止阻塞UI线程
            initApp()
            jumpToWebActivty(WebActivity::class.java)
        }
    }

    //应用在跳转到WebView前所需要做的所有初始化操作
    private fun initApp() {
        HttpServer.createInstance()
        DatabaseUtils.initDaoInstances()
        initLavsources()
        RecommendedVideoPool.init()
    }

    private fun initLavsources() {
        LavsourceMonitorService.createAndStart()
        val list = LavsourceInfoDao.listEnabled()
        if(list.isEmpty()) return
        val startTime = System.currentTimeMillis()
        while(true) {
            list.forEach {
                runCatching {
                    val packageName = it.packageName!!
                    if(LavsourceUtils.getLavsourceStatus(packageName)) return
                }
                //等待时间太短可能会导致设备内存（RAM）被迅速耗尽
                TimeUnit.SECONDS.sleep(1)
                if(System.currentTimeMillis() - startTime < 30 * 1000) return@forEach
                throw Exception("LavSources status check timeout")
            }
        }
    }
}