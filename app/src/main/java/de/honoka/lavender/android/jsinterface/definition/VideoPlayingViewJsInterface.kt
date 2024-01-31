package de.honoka.lavender.android.jsinterface.definition

import android.webkit.JavascriptInterface
import de.honoka.lavender.android.ui.WebActivity

class VideoPlayingViewJsInterface(
    private val webActivity: WebActivity
) {

    @JavascriptInterface
    fun simulateClickBeforeVideoPlay() {
        webActivity.simulateClick(webActivity.webView.width / 2f, 0f)
    }
}