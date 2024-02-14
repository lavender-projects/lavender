package de.honoka.lavender.android.jsinterface

import de.honoka.lavender.android.util.RecommendedVideoPool
import de.honoka.lavender.api.data.RecommendedVideoItem
import de.honoka.sdk.util.android.jsinterface.async.AsyncJavascriptInterface

class VideoJsInterface {

    @AsyncJavascriptInterface
    fun recommendedVideoList(): List<RecommendedVideoItem> = RecommendedVideoPool.takeOutVideos(10)
}