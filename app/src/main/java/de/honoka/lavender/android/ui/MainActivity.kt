package de.honoka.lavender.android.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import de.honoka.lavender.android.R
import de.honoka.lavender.android.util.DatabaseUtils
import de.honoka.sdk.util.android.common.GlobalComponents
import de.honoka.sdk.util.android.server.HttpServer
import de.honoka.sdk.util.android.server.HttpServerUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreenToShow()
        setContentView(R.layout.activity_main)
        GlobalComponents.application = application
        CoroutineScope(Dispatchers.IO).launch {
            //init可能是一个耗时的操作，故在IO线程中执行，防止阻塞UI线程
            initHttpServer()
            DatabaseUtils.initDaoInstances()
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

    private fun jumpToWebActivty() = runOnUiThread {
        startActivity(Intent(this, WebActivity::class.java).apply {
            putExtra("firstWebActivity", true)
        })
        finish()
    }
}