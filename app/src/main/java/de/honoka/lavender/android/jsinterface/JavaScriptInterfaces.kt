package de.honoka.lavender.android.jsinterface

import de.honoka.lavender.android.jsinterface.async.AsyncTaskJsInterface
import de.honoka.lavender.android.jsinterface.definition.BasicJsInterface
import de.honoka.lavender.android.jsinterface.definition.LavsourceJsInterface
import de.honoka.lavender.android.jsinterface.definition.VideoPlayingViewJsInterface
import de.honoka.lavender.android.ui.WebActivity

class JavaScriptInterfaces(private val webActivity: WebActivity) {

    private val interfaceInstances = arrayListOf(
        BasicJsInterface(webActivity),
        VideoPlayingViewJsInterface(webActivity),
        LavsourceJsInterface(webActivity)
    )

    val interfaces = HashMap<String, Any>().apply {
        interfaceInstances.add(AsyncTaskJsInterface(this@JavaScriptInterfaces, webActivity))
        interfaceInstances.forEach {
            this[it.javaClass.simpleName] = it
        }
    }

    init {
        registerJsInterfaces()
    }

    private fun registerJsInterfaces() {
        interfaceInstances.forEach {
            webActivity.webView.addJavascriptInterface(it, "android_${it.javaClass.simpleName}")
        }
    }
}