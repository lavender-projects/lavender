package de.honoka.lavender.android.jsinterface.definition

import android.graphics.Bitmap
import android.webkit.JavascriptInterface
import androidx.core.graphics.drawable.toBitmap
import cn.hutool.core.io.FileUtil
import de.honoka.lavender.android.data.LavsourceInfo
import de.honoka.lavender.android.jsinterface.async.AsyncJavascriptInterface
import de.honoka.lavender.android.ui.WebActivity
import de.honoka.lavender.android.util.ApiResponse
import de.honoka.lavender.android.util.GlobalData
import de.honoka.lavender.android.util.ServerVariables
import java.io.ByteArrayOutputStream
import java.io.File

class LavsourceJsInterface(
    private val webActivity: WebActivity
) {

    @AsyncJavascriptInterface(isPlainText = false)
    @JavascriptInterface
    fun getLocalLavsourceListCanBeAdded(): ApiResponse<List<LavsourceInfo>> {
        val lavsourceIconPath = "${GlobalData.application.filesDir}/lavsource/local/icon".also {
            File(it).apply {
                if(exists()) FileUtil.del(this)
                mkdirs()
            }
        }
        val result = ArrayList<LavsourceInfo>()
        @Suppress("DEPRECATION")
        webActivity.packageManager.getInstalledApplications(0).forEach {
            val isLavsourceApp = it.packageName.startsWith("lavsource.") ||
                it.packageName.endsWith(".lavsource") ||
                it.packageName.contains(".lavsource.")
            if(!isLavsourceApp) return@forEach
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
            result.add(LavsourceInfo().apply {
                type = LavsourceInfo.Type.LOCAL
                name = webActivity.packageManager.getApplicationLabel(it).toString()
                packageName = it.packageName
                imgUrl = ServerVariables.getImageUrlByWebServerPrefix(
                    "/files/lavsource/local/icon/${it.packageName}.png"
                )
            })
        }
        return ApiResponse.success(result)
    }
}