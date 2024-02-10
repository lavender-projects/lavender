package de.honoka.lavender.android.jsinterface

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import cn.hutool.core.bean.BeanUtil
import cn.hutool.core.bean.copier.CopyOptions
import cn.hutool.core.io.FileUtil
import de.honoka.lavender.android.dao.LavsourceInfoDao
import de.honoka.lavender.android.data.LavsourceAddParams
import de.honoka.lavender.android.data.LavsourceInfoVo
import de.honoka.lavender.android.entity.LavsourceInfo
import de.honoka.lavender.android.ui.WebActivity
import de.honoka.sdk.util.android.common.GlobalComponents
import de.honoka.sdk.util.android.common.SnowflakeUtils
import de.honoka.sdk.util.android.jsinterface.async.AsyncJavascriptInterface
import de.honoka.sdk.util.android.server.HttpServerVariables
import java.io.ByteArrayOutputStream
import java.io.File

class LavsourceJsInterface(private val webActivity: WebActivity) {

    private fun getLocalLavsourceIconRelativePath(packageName: String?) = run {
        "/files/lavsource/local/icon/$packageName.png"
    }

    private fun getSavedLavsourceIconRelativePath(id: String) = "/files/lavsource/saved/icon/$id.png"

    @AsyncJavascriptInterface
    fun getLocalLavsourceListCanBeAdded(): List<LavsourceInfoVo> {
        val lavsourceIconPath = "${GlobalComponents.application.filesDir}/lavsource/local/icon".also {
            File(it).apply {
                if(exists()) FileUtil.del(this)
                mkdirs()
            }
        }
        val existLavsourcesPackageNameList = LavsourceInfoDao.listAll().map { it.packageName }
        val result = ArrayList<LavsourceInfoVo>()
        @Suppress("DEPRECATION")
        webActivity.packageManager.getInstalledApplications(0).forEach {
            val isLavsourceApp = it.packageName.startsWith("lavsource.") ||
                it.packageName.endsWith(".lavsource") ||
                it.packageName.contains(".lavsource.")
            if(!isLavsourceApp || existLavsourcesPackageNameList.contains(it.packageName)) {
                return@forEach
            }
            val iconBytes = ByteArrayOutputStream().run {
                val icon = webActivity.packageManager.getApplicationIcon(it)
                icon.toBitmap().compress(Bitmap.CompressFormat.PNG, 100, this)
                toByteArray()
            }
            File("${lavsourceIconPath}/${it.packageName}.png").run {
                createNewFile()
                outputStream().use { out ->
                    out.write(iconBytes)
                }
            }
            result.add(LavsourceInfoVo().apply {
                type = LavsourceInfo.Type.LOCAL
                name = webActivity.packageManager.getApplicationLabel(it).toString()
                packageName = it.packageName
                iconUrl = HttpServerVariables.getImageUrlByPrefix(
                    getLocalLavsourceIconRelativePath(packageName)
                )
            })
        }
        return result
    }

    @AsyncJavascriptInterface
    fun addLocalLavsource(params: LavsourceAddParams) {
        val info = LavsourceInfo().apply {
            id = SnowflakeUtils.nextId().toString()
            BeanUtil.copyProperties(params, this, CopyOptions.create().ignoreNullValue())
        }
        val imgFile = File("${GlobalComponents.application.dataDir.absolutePath}${
            getLocalLavsourceIconRelativePath(params.packageName)
        }").apply {
            if(!exists()) throw Exception("No such image: $absolutePath")
        }
        File("${GlobalComponents.application.dataDir}${getSavedLavsourceIconRelativePath(info.id!!)}").let {
            FileUtil.copy(imgFile, it, true)
        }
        LavsourceInfoDao.save(info)
    }

    @AsyncJavascriptInterface
    fun getExistingLavsourceList(): List<LavsourceInfoVo> = LavsourceInfoDao.listAll().map {
        LavsourceInfoVo().apply {
            BeanUtil.copyProperties(it, this)
            iconUrl = HttpServerVariables.getImageUrlByPrefix(
                getSavedLavsourceIconRelativePath(it.id!!)
            )
        }
    }

    @AsyncJavascriptInterface
    fun getLavsourceStatus(id: String): Boolean {
        val lavsourceInfo = LavsourceInfoDao.getById(id)
        lavsourceInfo ?: return false
        val bundle = GlobalComponents.application.contentResolver.call(
            Uri.parse("content://${lavsourceInfo.packageName}.provider.LavsourceProvider"),
            "", null, null
        )
        return true
    }
}