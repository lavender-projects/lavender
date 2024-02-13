package de.honoka.lavender.android.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import de.honoka.lavender.android.R
import de.honoka.lavender.android.dao.LavsourceInfoDao
import de.honoka.lavender.android.service.LavsourceMonitorService
import de.honoka.lavender.android.util.DatabaseUtils
import de.honoka.lavender.android.util.LavsourceUtils
import de.honoka.sdk.util.android.common.GlobalComponents
import de.honoka.sdk.util.android.common.launchCoroutineOnIoThread
import de.honoka.sdk.util.android.server.HttpServer
import de.honoka.sdk.util.android.server.HttpServerUtils
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreenToShow()
        setContentView(R.layout.activity_main)
        GlobalComponents.application = application
        launchCoroutineOnIoThread {
            //init可能是一个耗时的操作，故在IO线程中执行，防止阻塞UI线程
            initHttpServer()
            DatabaseUtils.initDaoInstances()
            initLavsources()
            jumpToWebActivty()
        }
    }

    /**
     * 全屏化当前Activity
     */
    @Suppress("DEPRECATION")
    private fun fullScreenToShow() {
        //隐藏状态栏（手机时间、电量等信息显示的地方）
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //隐藏虚拟按键
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    private fun initHttpServer() {
        HttpServerUtils.initServerPorts()
        HttpServer.createInstance()
    }

    private fun initLavsources() {
        val list = LavsourceInfoDao.listEnabled()
        if(list.isEmpty()) return
        LavsourceMonitorService.createAndStart()
        val startTime = System.currentTimeMillis()
        while(true) {
            list.forEach {
                val packageName = it.packageName!!
                if(LavsourceUtils.getLavsourceStatus(packageName)) {
                    LavsourceMonitorService.baseUrlMap[it.id!!] = LavsourceUtils.getLavsourceBaseUrl(packageName)!!
                    return
                }
                //等待时间太短可能会导致设备内存（RAM）被迅速耗尽
                TimeUnit.SECONDS.sleep(1)
                if(System.currentTimeMillis() - startTime < 30 * 1000) return@forEach
                throw Exception("LavSources status check timeout")
            }
        }
    }

    private fun jumpToWebActivty() = runOnUiThread {
        startActivity(Intent(this, WebActivity::class.java).apply {
            putExtra("firstWebActivity", true)
        })
        finish()
    }
}