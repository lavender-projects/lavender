package de.honoka.lavender.android.jsinterface

import cn.hutool.json.JSONObject
import de.honoka.lavender.android.util.RecommendedVideoPool
import de.honoka.lavender.api.data.*
import de.honoka.sdk.util.android.jsinterface.async.AsyncJavascriptInterface

class VideoJsInterface {

    @AsyncJavascriptInterface
    fun recommendedVideoList(): List<RecommendedVideoItem> = RecommendedVideoPool.takeOutVideos(10)

    @AsyncJavascriptInterface
    fun videoDetails(params: JSONObject): VideoDetails = run {
        TODO()
    }

    @AsyncJavascriptInterface
    fun commentList(params: JSONObject): CommentList = run {
        TODO()
    }

    @AsyncJavascriptInterface
    fun commentReplyList(params: JSONObject): CommentList = run {
        TODO()
    }

    @AsyncJavascriptInterface
    fun danmakuList(params: JSONObject): List<DanmakuInfo> = run {
        TODO()
    }

    @AsyncJavascriptInterface
    fun episodeInfoList(params: JSONObject): List<VideoEpisodeInfo> = run {
        TODO()
    }

    @AsyncJavascriptInterface
    fun streamInfoList(params: JSONObject): List<VideoStreamInfo> = run {
        TODO()
    }
}