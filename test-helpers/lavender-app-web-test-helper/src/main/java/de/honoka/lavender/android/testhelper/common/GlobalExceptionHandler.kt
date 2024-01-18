package de.honoka.lavender.android.testhelper.common

import de.honoka.sdk.util.framework.web.ApiException
import de.honoka.sdk.util.framework.web.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletResponse

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(Throwable::class)
    fun handleAll(t: Throwable, response: HttpServletResponse): ApiResponse<*> {
        response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        val responseBody = ApiResponse.fail(t.message)
        if((t is ApiException && t.isPrintStackTrace) || t !is ApiException) {
            log.error("Controller method error", t)
        }
        return responseBody
    }
}