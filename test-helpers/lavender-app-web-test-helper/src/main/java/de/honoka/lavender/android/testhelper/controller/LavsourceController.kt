package de.honoka.lavender.android.testhelper.controller

import de.honoka.lavender.android.testhelper.MainProperties
import de.honoka.lavender.android.testhelper.data.LavsourceInfo
import de.honoka.sdk.util.framework.web.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/lavsource")
@RestController
class LavsourceController(
    private val mainProperties: MainProperties
) {

    @GetMapping("/lavsourceListCanBeAdded")
    fun lavsourceListCanBeAdded(): ApiResponse<*> = ApiResponse.success(listOf(
        LavsourceInfo().apply {
            name = "LavSource bilibili"
            packageName = "de.honoka.lavender.lavsource.bilibili"
            imgUrl = "http://${mainProperties.remoteAccessHostName}:${mainProperties.serverPort}/img/lavsource_bilibili.png"
        }
    ))
}