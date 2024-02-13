package de.honoka.lavender.android.util

import cn.hutool.json.JSONObject
import de.honoka.sdk.util.android.common.contentResolverCall

object LavsourceUtils {

    fun getLavsourceStatus(packageName: String): Boolean = contentResolverCall<JSONObject>(
        "${packageName}.provider.LavsourceProvider"
    ).getBool("status")

    fun getLavsourceBaseUrl(packageName: String): String? = contentResolverCall(
        "${packageName}.provider.LavsourceProvider",
        "getBaseUrl"
    ) as String?
}