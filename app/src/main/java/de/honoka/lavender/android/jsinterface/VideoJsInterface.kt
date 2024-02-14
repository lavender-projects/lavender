package de.honoka.lavender.android.jsinterface

import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.lavender.android.service.LavsourceMonitorService
import de.honoka.lavender.android.util.RecommendedVideoPool
import de.honoka.lavender.api.data.RecommendedVideoItem
import de.honoka.lavender.api.data.VideoDetails
import de.honoka.sdk.util.android.jsinterface.async.AsyncJavascriptInterface
import de.honoka.sdk.util.kotlin.hutool.params

class VideoJsInterface {

    @AsyncJavascriptInterface
    fun recommendedVideoList(): List<RecommendedVideoItem> = RecommendedVideoPool.takeOutVideos(10)

    @AsyncJavascriptInterface
    fun videoDetails(params: JSONObject): VideoDetails {
        val lavsourceId = params["lavsourceId"] as String
        val urlPrefix = LavsourceMonitorService.baseUrlMap[lavsourceId]
        val res = HttpUtil.createGet("$urlPrefix/video/details").params(params).execute().body()
        return JSONUtil.parseObj(res).getJSONObject("data").toBean(VideoDetails::class.java).apply {
            this.lavsourceId = lavsourceId
        }
    }
}