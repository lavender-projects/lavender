package de.honoka.lavender.android.ui

import de.honoka.lavender.android.jsinterface.LavsourceJsInterface
import de.honoka.lavender.android.jsinterface.VideoJsInterface
import de.honoka.lavender.android.jsinterface.VideoPlayingViewJsInterface
import de.honoka.lavender.android.service.LavsourceMonitorService
import de.honoka.sdk.util.android.ui.AbstractWebActivity

class WebActivity : AbstractWebActivity() {

    override val definedJsInterfaceInstances: List<Any> = listOf(
        LavsourceJsInterface(),
        VideoJsInterface(),
        VideoPlayingViewJsInterface(this)
    )

    override fun extendedOnResume() {
        LavsourceMonitorService.checkOrRestartAsync()
    }
}