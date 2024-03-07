package de.honoka.lavender.android.ui

import android.annotation.SuppressLint
import de.honoka.lavender.android.service.LavsourceMonitorService
import de.honoka.lavender.android.util.JsInterfaceContainerFactory
import de.honoka.sdk.util.android.jsinterface.JavascriptInterfaceContainer
import de.honoka.sdk.util.android.ui.AbstractWebActivity

@SuppressLint("SetJavaScriptEnabled")
class WebActivity : AbstractWebActivity() {

    override fun extendedOnResume() {
        LavsourceMonitorService.checkOrRestartAsync()
    }

    override fun newJavascriptInterfaceContainer(): JavascriptInterfaceContainer = run {
        JsInterfaceContainerFactory(this@WebActivity).getContainer()
    }
}