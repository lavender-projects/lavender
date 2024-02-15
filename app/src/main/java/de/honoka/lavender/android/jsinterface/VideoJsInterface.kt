package de.honoka.lavender.android.jsinterface

import cn.hutool.json.JSONObject
import de.honoka.lavender.android.util.LavsourceUtils
import de.honoka.lavender.android.util.RecommendedVideoPool
import de.honoka.lavender.api.data.*
import de.honoka.sdk.util.android.jsinterface.async.AsyncJavascriptInterface

class VideoJsInterface {

    @AsyncJavascriptInterface
    fun recommendedVideoList(): List<RecommendedVideoItem> = RecommendedVideoPool.takeOutVideos(10)

    @AsyncJavascriptInterface
    fun videoDetails(params: JSONObject): VideoDetails = run {
        LavsourceUtils.httpGetForObject<VideoDetails>("/video/details", params)
    }

    @AsyncJavascriptInterface
    fun commentList(params: JSONObject): CommentList = run {
        LavsourceUtils.httpGetForObject<CommentList>("/video/comment/list", params)
    }

    @AsyncJavascriptInterface
    fun commentReplyList(params: JSONObject): CommentList = run {
        LavsourceUtils.httpGetForObject<CommentList>("/video/comment/reply/list", params)
    }

    @AsyncJavascriptInterface
    fun danmakuList(params: JSONObject): List<DanmakuInfo> = run {
        LavsourceUtils.httpGetForList<DanmakuInfo>("/video/danmaku/list", params)
    }

    @AsyncJavascriptInterface
    fun episodeInfoList(params: JSONObject): List<VideoEpisodeInfo> = run {
        LavsourceUtils.httpGetForList<VideoEpisodeInfo>("/video/episode/list", params)
    }

    @AsyncJavascriptInterface
    fun streamInfoList(params: JSONObject): List<VideoStreamInfo> = run {
        LavsourceUtils.httpGetForList<VideoStreamInfo>("/video/stream/urlList", params)
    }
}