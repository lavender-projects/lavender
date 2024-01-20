package de.honoka.lavender.android.jsinterface.async

import android.webkit.JavascriptInterface
import cn.hutool.core.thread.BlockPolicy
import com.alibaba.fastjson2.JSON
import de.honoka.lavender.android.jsinterface.JavaScriptInterfaces
import de.honoka.lavender.android.ui.WebActivity
import java.lang.reflect.Method
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class AsyncTaskJsInterface(
    private val javaScriptInterfaces: JavaScriptInterfaces,
    private val webActivity: WebActivity
) {

    private val threadPool = ThreadPoolExecutor(
        5, 30, 60, TimeUnit.SECONDS,
        LinkedBlockingQueue(), BlockPolicy()
    )

    @JavascriptInterface
    fun invokeAsyncMethod(jsInterfaceName: String, methodName: String, callbackId: String, args: String) {
        threadPool.submit {
            val result = AsyncTaskResult()
            try {
                val jsInterface = javaScriptInterfaces.interfaces[jsInterfaceName].also {
                    it ?: throw Exception("Unknown JavaScript interface name: $jsInterfaceName")
                }
                val method: Method = jsInterface!!.javaClass.declaredMethods.run {
                    forEach {
                        if(it.name == methodName) return@run it
                    }
                    throw Exception("Unknown method name \"$methodName\" of interface name: $jsInterfaceName")
                }
                val annotation = method.getAnnotation(AsyncJavascriptInterface::class.java).also {
                    it ?: throw Exception("Method \"$methodName\" in interface \"$jsInterfaceName\" is not asynchronous")
                }
                result.run {
                    isPlainText = annotation!!.isPlainText
                    val methodArgs = JSON.parseArray(args).map { it?.toString() }.toTypedArray()
                    this.result = method.invoke(jsInterface, *methodArgs)?.toString()
                    isResolve = true
                }
            } catch(t: Throwable) {
                result.run {
                    isPlainText = true
                    isResolve = false
                    this.result = t.message
                }
            }
            val resultStr = JSON.toJSONString(result)
            val script = "window.jsInterfaceAsyncMethodCallbackUtils.invokeCallback('$callbackId', $resultStr)"
            webActivity.evaluateJavascript(script)
        }
    }
}