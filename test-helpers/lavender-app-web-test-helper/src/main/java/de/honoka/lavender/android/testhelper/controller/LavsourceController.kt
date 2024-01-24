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

    @GetMapping("/localLavsourceListCanBeAdded")
    fun localLavsourceListCanBeAdded(): ApiResponse<*> = ApiResponse.success(listOf(
        LavsourceInfo().apply {
            id = 1
            type = "local"
            name = "LavSource bilibili"
            packageName = "de.honoka.lavender.lavsource.bilibili"
            imgUrl = "http://${mainProperties.remoteAccessHostName}:${mainProperties.serverPort}/img/lavsource_bilibili.png"
        },
        LavsourceInfo().apply {
            id = 2
            type = "local"
            name = "test1"
            packageName = "test1"
            imgUrl = "http://${mainProperties.remoteAccessHostName}:${mainProperties.serverPort}/img/lavsource_bilibili.png"
        },
        LavsourceInfo().apply {
            id = 3
            type = "local"
            name = "test2"
            packageName = "test2"
            imgUrl = "http://${mainProperties.remoteAccessHostName}:${mainProperties.serverPort}/img/lavsource_bilibili.png"
        }
    ))
}