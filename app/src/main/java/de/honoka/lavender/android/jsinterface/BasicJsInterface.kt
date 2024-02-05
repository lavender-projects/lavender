package de.honoka.lavender.android.jsinterface

import android.content.Intent
import android.webkit.JavascriptInterface
import de.honoka.lavender.android.ui.WebActivity
import de.honoka.sdk.util.android.server.HttpServerVariables

class BasicJsInterface(private val webActivity: WebActivity) {

    @JavascriptInterface
    fun openNewWebActivity(path: String) {
        webActivity.run {
            startActivity(Intent(this, WebActivity::class.java).apply {
                putExtra("url", HttpServerVariables.getUrlByWebServerPrefix(path))
            })
        }
    }

    @JavascriptInterface
    fun finishCurrentWebActivity() {
        webActivity.finish()
    }
}