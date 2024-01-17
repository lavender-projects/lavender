package de.honoka.lavender.android.util

import android.content.Intent
import android.webkit.JavascriptInterface
import de.honoka.lavender.android.ui.WebActivity

object JavaScriptInterfaces {

    fun newAll(webActivity: WebActivity) = arrayOf(
        BasicJsInterface(webActivity),
        VideoPlayingViewJsInterface(webActivity)
    )
}

class BasicJsInterface(
    private val webActivity: WebActivity
) {

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

class VideoPlayingViewJsInterface(
    private val webActivity: WebActivity
) {

    @JavascriptInterface
    fun simulateClickBeforeVideoPlay() {
        webActivity.simulateClick(webActivity.webView.width / 2f, 0f)
    }
}