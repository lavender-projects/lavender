package de.honoka.lavender.android.util

import de.honoka.lavender.android.dao.LavsourceInfoDao
import de.honoka.lavender.android.lavsource.sdk.business.stub.VideoBusinessStub
import de.honoka.lavender.api.data.RecommendedVideoItem
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import kotlin.math.min

object RecommendedVideoPool {

    private val pool = ConcurrentLinkedQueue<RecommendedVideoItem>()

    private val thread = Thread {
        while(true) {
            if(Thread.currentThread().isInterrupted) return@Thread
            try {
                TimeUnit.SECONDS.sleep(5)
            } catch(t: Throwable) {
                return@Thread
            }
            runCatching {
                if(pool.size < 100) fetchVideosFromLavsources()
            }
        }
    }

    private var initialized = false

    private var lavsourceIdCacheSet: Set<String?>? = null

    fun init() {
        if(initialized) return
        fetchVideosFromLavsources()
        thread.start()
        initialized = true
    }

    private fun fetchVideosFromLavsources() {
        val newVideoList = ArrayList<RecommendedVideoItem>()
        LavsourceInfoDao.listEnabled().forEach {
            runCatching {
                val videoList = VideoBusinessStub(it.packageName!!).getRecommendedVideoList()
                videoList.forEach { video ->
                    video.lavsourceId = it.id
                }
                newVideoList.addAll(videoList)
            }
        }
        synchronized(this) {
            pool.addAll(newVideoList)
            removeVideosOfDisabledLavsource()
        }
    }

    fun removeVideosOfDisabledLavsource() {
        val newLavsourceIdCacheSet = LavsourceInfoDao.listEnabled().map { it.id }.toSet()
        var shouldReturn = false
        lavsourceIdCacheSet?.let {
            shouldReturn = it.size == newLavsourceIdCacheSet.size && it.containsAll(newLavsourceIdCacheSet)
        }
        lavsourceIdCacheSet = newLavsourceIdCacheSet
        if(shouldReturn) return
        pool.removeIf { !lavsourceIdCacheSet!!.contains(it.lavsourceId) }
    }

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
        val count = min(pool.size, maxCount)
        return ArrayList<RecommendedVideoItem>().apply {
            for(i in 0 until count) {
                pool.poll()?.let { add(it) }
            }
        }
    }
}