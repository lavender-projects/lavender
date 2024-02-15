package de.honoka.lavender.android.util

import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONObject
import cn.hutool.json.JSONUtil
import de.honoka.lavender.android.service.LavsourceMonitorService
import de.honoka.lavender.api.util.MultipleLavsourceIdContainer
import de.honoka.sdk.util.android.common.contentResolverCall
import de.honoka.sdk.util.kotlin.hutool.params

object LavsourceUtils {

    fun getLavsourceStatus(packageName: String): Boolean = contentResolverCall<JSONObject>(
        "${packageName}.provider.LavsourceProvider"
    ).getBool("status")

    fun getLavsourceBaseUrl(packageName: String): String? = contentResolverCall(
        "${packageName}.provider.LavsourceProvider",
        "getBaseUrl"
    ) as String?

    inline fun <reified T> httpGetForObject(urlPath: String, params: JSONObject): T {
        val lavsourceId = params["lavsourceId"] as String
        val urlPrefix = LavsourceMonitorService.baseUrlMap[lavsourceId]
        val res = HttpUtil.createGet("$urlPrefix$urlPath").params(params).execute().body()
        return JSONUtil.parseObj(res).getJSONObject("data").toBean(T::class.java).also {
            if(it is MultipleLavsourceIdContainer) it.setMultipleLavsourceId(lavsourceId)
        }
    }
}