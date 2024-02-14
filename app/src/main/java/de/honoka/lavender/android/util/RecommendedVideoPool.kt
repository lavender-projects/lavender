package de.honoka.lavender.android.util

import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.lavender.android.service.LavsourceMonitorService
import de.honoka.lavender.api.data.RecommendedVideoItem
import java.util.concurrent.TimeUnit
import kotlin.math.min

object RecommendedVideoPool {

    private val pool = ArrayList<RecommendedVideoItem>()

    private val thread = Thread {
        while(true) {
            if(Thread.currentThread().isInterrupted) return@Thread
            runCatching {
                if(pool.size < 100) fetchVideosFromLavsources()
            }
            TimeUnit.SECONDS.sleep(5)
        }
    }

    private var initialized = false

    private var lavsourceIdCacheSet: Set<String>? = null

    fun init() {
        if(initialized) return
        fetchVideosFromLavsources()
        thread.start()
        initialized = true
    }

    private fun fetchVideosFromLavsources() {
        val newVideoList = ArrayList<RecommendedVideoItem>()
        LavsourceMonitorService.baseUrlMap.forEach { entry ->
            runCatching {
                val res = HttpUtil.get("${entry.value}/video/recommended")
                JSONUtil.parseObj(res).getJSONArray("data").map {
                    it as JSONObject
                    it.toBean(RecommendedVideoItem::class.java).apply {
                        lavsourceId = entry.key
                    }
                }.let { newVideoList.addAll(it) }
            }
        }
        synchronized(this) {
            pool.addAll(newVideoList)
            removeVideosOfDisabledLavsource()
        }
    }

    private fun removeVideosOfDisabledLavsource() {
        val newLavsourceIdCacheSet = HashSet(LavsourceMonitorService.baseUrlMap.keys)
        var shouldReturn = false
        lavsourceIdCacheSet?.let {
            shouldReturn = it.size == newLavsourceIdCacheSet.size && it.containsAll(newLavsourceIdCacheSet)
        }
        lavsourceIdCacheSet = newLavsourceIdCacheSet
        if(shouldReturn) return
        pool.removeIf { !lavsourceIdCacheSet!!.contains(it.lavsourceId) }
    }

    @Synchronized
    fun takeOutVideos(maxCount: Int): List<RecommendedVideoItem> {
        if(pool.isEmpty()) {
            //此处不需要调用removeVideosOfDisabledLavsource方法
            //fetchVideosFromLavsources方法中已包含removeVideosOfDisabledLavsource操作
            fetchVideosFromLavsources()
        } else {
            removeVideosOfDisabledLavsource()
        }
        //removeVideosOfDisabledLavsource之后pool可能为空
        if(pool.isEmpty()) return emptyList()
        val subList = pool.subList(0, min(pool.size, maxCount))
        return ArrayList(subList).apply {
            subList.clear()
        }
    }
}