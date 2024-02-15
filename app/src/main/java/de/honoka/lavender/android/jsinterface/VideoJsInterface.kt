package de.honoka.lavender.android.jsinterface

import cn.hutool.json.JSONObject
import de.honoka.lavender.android.util.LavsourceUtils
import de.honoka.lavender.android.util.RecommendedVideoPool
import de.honoka.lavender.api.data.CommentList
import de.honoka.lavender.api.data.RecommendedVideoItem
import de.honoka.lavender.api.data.VideoDetails
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
}