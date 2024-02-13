package de.honoka.lavender.android.util

import cn.hutool.json.JSONObject
import de.honoka.sdk.util.android.common.contentResolverCall

object LavsourceUtils {

    fun getLavsourceStatus(packageName: String): Boolean {
        return contentResolverCall<JSONObject>(
            "${packageName}.provider.LavsourceProvider"
        ).getBool("status")
    }
}