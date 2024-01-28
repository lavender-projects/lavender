package de.honoka.lavender.android.jsinterface.definition

import android.content.Intent
import android.webkit.JavascriptInterface
import de.honoka.lavender.android.ui.WebActivity
import de.honoka.lavender.android.util.ServerVariables

class BasicJsInterface(private val webActivity: WebActivity) {

    @JavascriptInterface
    fun openNewWebActivity(path: String) {
        webActivity.run {
            startActivity(Intent(this, WebActivity::class.java).apply {
                putExtra("url", ServerVariables.getUrlByWebServerPrefix(path))
            })
        }
    }

    @JavascriptInterface
    fun finishCurrentWebActivity() {
        webActivity.finish()
    }
}