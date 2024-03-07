package de.honoka.lavender.android.util

import cn.hutool.json.JSONArray
import cn.hutool.json.JSONObject
import de.honoka.lavender.android.lavsource.sdk.provider.LavsourceProviderRequest
import de.honoka.lavender.api.business.BasicBusiness
import de.honoka.sdk.util.android.common.contentResolverCall
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

object LavsourceUtils {

    inline fun <reified T> callLavsourceProvider(
        packageName: String, businessMethod: KFunction<*>, args: Iterable<Any?>? = null
    ): T {
        val request = LavsourceProviderRequest().apply {
            className = businessMethod.javaMethod!!.declaringClass.simpleName
            method = businessMethod.name
            args?.let { this.args = JSONArray(args, false) }
        }
        return contentResolverCall<T>("${packageName}.provider.LavsourceProvider", args = request)
    }

    fun getLavsourceStatus(packageName: String): Boolean = callLavsourceProvider<JSONObject>(
        packageName, BasicBusiness::statusCheck
    ).getBool("status")
}