package de.honoka.lavender.android.util

import de.honoka.lavender.android.jsinterface.BasicJsInterface
import de.honoka.lavender.android.jsinterface.LavsourceJsInterface
import de.honoka.lavender.android.jsinterface.VideoPlayingViewJsInterface
import de.honoka.lavender.android.ui.WebActivity
import de.honoka.sdk.util.android.jsinterface.AbstractJavascriptInterfaceContainerFactory
import de.honoka.sdk.util.android.jsinterface.JavascriptInterfaceContainer

class JsInterfaceContainerFactory(private val webActivity: WebActivity) : AbstractJavascriptInterfaceContainerFactory() {

    override val containerInstance: JavascriptInterfaceContainer by lazy {
        JavascriptInterfaceContainer(interfaceInstances, webActivity.webView)
    }

    override val interfaceInstances: List<Any> = listOf(
        BasicJsInterface(webActivity),
        LavsourceJsInterface(),
        VideoPlayingViewJsInterface(webActivity)
    )
}