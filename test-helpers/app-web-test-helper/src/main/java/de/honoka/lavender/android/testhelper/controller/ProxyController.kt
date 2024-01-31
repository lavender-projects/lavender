package de.honoka.lavender.android.testhelper.controller

import cn.hutool.http.HttpUtil
import cn.hutool.http.Method
import de.honoka.lavender.android.testhelper.MainProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RequestMapping("/proxy")
@RestController
class ProxyController(
    private val mainProperties: MainProperties
) {

    @GetMapping("/{*path}", produces = [ MediaType.APPLICATION_JSON_VALUE ])
    fun onRequest(
        @PathVariable path: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String? = onRequest(path, request.queryString, request, response)

    @RequestMapping("/{*path}", produces = [ MediaType.APPLICATION_JSON_VALUE ])
    fun onRequest(
        @PathVariable path: String,
        @RequestBody body: String?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String? {
        val url = "${mainProperties.proxyPassUrlPrefix}$path"
        val remoteResponse = when(request.method.lowercase()) {
            "get" -> HttpUtil.createGet("$url?$body").run {
                header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                header(HttpHeaders.ACCEPT_CHARSET, "UTF-8")
                execute()
            }
            "post", "put", "patch", "delete" -> {
                HttpUtil.createRequest(Method.valueOf(request.method.uppercase()), url).run {
                    setRest(true)
                    header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    header(HttpHeaders.ACCEPT_CHARSET, "UTF-8")
                    body(body)
                    execute()
                }
            }
            "options" -> null
            else -> throw Exception("Unknown request method: ${request.method}")
        }
        remoteResponse?.let {
            response.status = it.status
        }
        return remoteResponse?.body()
    }
}