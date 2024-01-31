package de.honoka.lavender.android.util

import android.webkit.MimeTypeMap
import cn.hutool.core.exceptions.ExceptionUtil
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import com.alibaba.fastjson2.JSONWriter
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.File

@Suppress("ConstPropertyName")
object ServerVariables {

    var webServerPort = 38081

    const val imageUrlPrefix = "/android/img"

    fun getUrlByWebServerPrefix(path: String) = "http://localhost:$webServerPort$path"

    fun getImageUrlByWebServerPrefix(path: String) = getUrlByWebServerPrefix("$imageUrlPrefix$path")
}

class WebServer(port: Int = ServerVariables.webServerPort) : NanoHTTPD(port) {

    companion object {

        lateinit var instance: WebServer

        private val staticResourcesPrefixes = arrayOf(
            "/assets", "/font", "/img", "/js", "/favicon.ico"
        )

        fun createInstance() {
            instance = WebServer().apply { start() }
        }

        fun checkOrRestartInstance() {
            if(instance.isAlive) return
            instance.start()
        }
    }

    override fun serve(session: IHTTPSession): Response {
        var path = session.uri
        if(path == "/") path = "/index.html"
        return try {
            handle(path)
        } catch(t: Throwable) {
            errorResponse(t)
        }
    }

    private fun handle(urlPath: String): Response {
        //判断路径是否匹配静态资源前缀
        staticResourcesPrefixes.forEach {
            //加载静态资源
            if(urlPath.startsWith(it)) return staticResourceResponse(urlPath)
        }
        androidImageResponse(urlPath)?.let { return it }
        //加载index.html
        return indexHtmlResponse()
    }

    private fun buildStaticResponse(urlPath: String, content: ByteArray): Response {
        val fileExt = urlPath.substring(urlPath.lastIndexOf(".") + 1)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt)
        return newFixedLengthResponse(Response.Status.OK, mimeType, ByteArrayInputStream(content), content.size.toLong())
    }

    private fun staticResourceResponse(urlPath: String): Response {
        val content = GlobalData.application.assets.open("web$urlPath").use { it.readBytes() }
        return buildStaticResponse(urlPath, content)
    }

    private fun indexHtmlResponse(): Response {
        val content = GlobalData.application.assets.open("web/index.html").use { it.readBytes() }
        return newFixedLengthResponse(Response.Status.OK, MIME_HTML, ByteArrayInputStream(content), content.size.toLong())
    }

    private fun androidImageResponse(urlPath: String): Response? {
        if(!urlPath.startsWith(ServerVariables.imageUrlPrefix)) return null
        val filePath = "${GlobalData.application.dataDir}${urlPath.substring(ServerVariables.imageUrlPrefix.length)}"
        val file = File(filePath)
        if(!file.exists()) return notFoundResponse(filePath)
        return buildStaticResponse(urlPath, file.readBytes())
    }

    private fun notFoundResponse(resourcePath: String): Response = newFixedLengthResponse(
        Response.Status.NOT_FOUND,
        MimeTypeMap.getSingleton().getMimeTypeFromExtension("json"),
        JSON.toJSONString(ApiResponse<Any>().apply {
            code = Response.Status.NOT_FOUND.requestStatus
            msg = "$resourcePath is not found"
        }, JSONWriter.Feature.PrettyFormat)
    )

    private fun errorResponse(t: Throwable): Response = newFixedLengthResponse(
        Response.Status.INTERNAL_ERROR,
        MimeTypeMap.getSingleton().getMimeTypeFromExtension("json"),
        JSON.toJSONString(ApiResponse<Any>().apply {
            code = Response.Status.INTERNAL_ERROR.requestStatus
            msg = t.message
            data = JSONObject().also {
                it["stackTrace"] = ExceptionUtil.stacktraceToString(t)
            }
        }, JSONWriter.Feature.PrettyFormat)
    )
}

object ServerUtils {

    private fun getOneAvaliablePort(startPort: Int): Int {
        var port = startPort
        var successful = false
        //验证端口可用性
        for(i in 0 until 10) {
            try {
                WebServer(port).apply {
                    start()
                    stop()
                }
                successful = true
                break
            } catch(t: Throwable) {
                port += 1
            }
        }
        if(!successful) throw RuntimeException("端口范围（$startPort - ${startPort + 10}）均被占用")
        return port
    }

    fun initServerPorts() {
        ServerVariables.webServerPort = getOneAvaliablePort(ServerVariables.webServerPort)
    }
}

data class ApiResponse<T>(

    var code: Int? = null,

    var msg: String? = null,

    var data: T? = null
) {

    companion object {

        fun <T> success(data: T? = null) = ApiResponse<T>().also {
            it.data = data
        }
    }

    override fun toString(): String = JSON.toJSONString(this)
}