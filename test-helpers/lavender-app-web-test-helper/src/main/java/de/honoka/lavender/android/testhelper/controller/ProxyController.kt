package de.honoka.lavender.android.testhelper.controller

import cn.hutool.http.HttpUtil
import cn.hutool.http.Method
import de.honoka.lavender.android.testhelper.MainProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class ProxyController(
    private val mainProperties: MainProperties
) {

    @GetMapping("/{*path}", produces = [ MediaType.APPLICATION_JSON_VALUE ])
    fun onRequest(
        @PathVariable path: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String = onRequest(path, null, request, response)

    @RequestMapping("/{*path}", produces = [ MediaType.APPLICATION_JSON_VALUE ])
    fun onRequest(
        @PathVariable path: String,
        @RequestBody body: String?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String {
        val url = "${mainProperties.proxyPassUrlPrefix}$path"
        return when(request.method.lowercase()) {
            "get" -> HttpUtil.createGet(url).run {
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                header(HttpHeaders.ACCEPT_CHARSET, "UTF-8")
                execute().body()
            }
            "post", "put", "patch", "delete" -> {
                HttpUtil.createRequest(Method.valueOf(request.method.uppercase()), url).run {
                    setRest(true)
                    header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    header(HttpHeaders.ACCEPT_CHARSET, "UTF-8")
                    body(body)
                    execute().body()
                }
            }
            "options" -> "{}"
            else -> throw Exception("Unknown request method: ${request.method}")
        }
    }
}