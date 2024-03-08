package de.honoka.lavender.android.jsinterface

import cn.hutool.json.JSONObject
import de.honoka.lavender.android.lavsource.sdk.business.stub.VideoBusinessStub
import de.honoka.lavender.android.util.LavsourceUtils
import de.honoka.lavender.android.util.RecommendedVideoPool
import de.honoka.lavender.api.data.*
import de.honoka.sdk.util.android.jsinterface.async.AsyncJavascriptInterface

class VideoJsInterface {

    private fun getStub(params: JSONObject) = VideoBusinessStub(LavsourceUtils.getPackageNameById(params))

    @AsyncJavascriptInterface
    fun recommendedVideoList(): List<RecommendedVideoItem> = RecommendedVideoPool.takeOutVideos(10)

    @AsyncJavascriptInterface
    fun videoDetails(params: JSONObject): VideoDetails = getStub(params).getVideoDetails(params.getStr("id"))

    @AsyncJavascriptInterface
    fun commentList(params: JSONObject): CommentList = getStub(params).getCommentList(
        params.getStr("videoId"),
        params.getStr("sortBy"),
        params.getInt("page")
    )

    @AsyncJavascriptInterface
    fun commentReplyList(params: JSONObject): CommentList = getStub(params).getCommentReplyList(
        params.getStr("videoId"),
        params.getStr("commentId"),
        params.getInt("page")
    )

    @AsyncJavascriptInterface
    fun danmakuList(params: JSONObject): List<DanmakuInfo> = getStub(params).getDanmakuList(
        params.getStr("episodeId")
    )

    @AsyncJavascriptInterface
    fun episodeInfoList(params: JSONObject): List<VideoEpisodeInfo> = getStub(params).getEpisodeList(
        params.getStr("videoId")
    )

    @AsyncJavascriptInterface
    fun streamInfoList(params: JSONObject): List<VideoStreamInfo> = getStub(params).getStreamUrlList(
        params.getStr("videoId"),
        params.getStr("episodeId")
    )
}