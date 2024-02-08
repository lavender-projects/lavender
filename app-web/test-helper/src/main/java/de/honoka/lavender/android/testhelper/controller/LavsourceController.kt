package de.honoka.lavender.android.testhelper.controller

import de.honoka.lavender.android.api.data.LavsourceInfoVo
import de.honoka.lavender.android.api.entity.LavsourceInfo
import de.honoka.lavender.android.testhelper.MainProperties
import de.honoka.sdk.util.framework.web.ApiResponse
import org.springframework.web.bind.annotation.*

@RequestMapping("/lavsource")
@RestController
class LavsourceController(
    private val mainProperties: MainProperties
) {

    @GetMapping("/localLavsourceListCanBeAdded")
    fun localLavsourceListCanBeAdded(): ApiResponse<*> = ApiResponse.success(listOf(
        LavsourceInfoVo().apply {
            id = 1
            type = "local"
            name = "LavSource bilibili"
            packageName = "de.honoka.lavender.lavsource.bilibili"
            iconUrl = "http://${mainProperties.remoteAccessHostName}:${mainProperties.serverPort}/img/lavsource_bilibili.png"
        },
        LavsourceInfoVo().apply {
            id = 2
            type = "local"
            name = "test1"
            packageName = "test1"
            iconUrl = "http://${mainProperties.remoteAccessHostName}:${mainProperties.serverPort}/img/lavsource_bilibili.png"
        },
        LavsourceInfoVo().apply {
            id = 3
            type = "local"
            name = "test2"
            packageName = "test2"
            iconUrl = "http://${mainProperties.remoteAccessHostName}:${mainProperties.serverPort}/img/lavsource_bilibili.png"
        }
    ))

    @PostMapping("/addLocalLavsource")
    fun addLocalLavsource(@RequestBody lavsourceInfo: LavsourceInfo): ApiResponse<*> = ApiResponse.success()

    @GetMapping("/existingLavsourceList")
    fun existingLavsourceList(): ApiResponse<*> = ApiResponse.success(listOf(
        LavsourceInfoVo().apply {
            id = 1
            type = "local"
            name = "LavSource bilibili"
            packageName = "de.honoka.lavender.lavsource.bilibili"
            iconUrl = "http://${mainProperties.remoteAccessHostName}:${mainProperties.serverPort}/img/lavsource_bilibili.png"
        },
        LavsourceInfoVo().apply {
            id = 2
            type = "network"
            name = "LavSource bilibili Network"
            iconUrl = "http://${mainProperties.remoteAccessHostName}:${mainProperties.serverPort}/img/lavsource_bilibili.png"
            baseUrl = "https://lavsource.bilibili.com/api"
        }
    ))
}