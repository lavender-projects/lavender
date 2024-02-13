package de.honoka.lavender.android.jsinterface

import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.lavender.android.service.LavsourceMonitorService
import de.honoka.lavender.api.data.RecommendedVideoItem
import de.honoka.sdk.util.android.jsinterface.async.AsyncJavascriptInterface

class VideoJsInterface {

    @AsyncJavascriptInterface
    fun recommendedVideoList(): List<RecommendedVideoItem> {
        val result = ArrayList<RecommendedVideoItem>()
        LavsourceMonitorService.baseUrlMap.forEach {
            JSONUtil.parseObj(HttpUtil.get("${it.value}/video/recommended")).getJSONArray("data").forEach { video ->
                video as JSONObject
                result.add(video.toBean(RecommendedVideoItem::class.java))
            }
        }
        return result
    }
}