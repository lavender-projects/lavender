package de.honoka.lavender.android.dao

import de.honoka.lavender.android.entity.LavsourceInfo
import de.honoka.sdk.util.android.common.GlobalComponents
import de.honoka.sdk.util.android.database.BaseDao

object LavsourceInfoDao : BaseDao<LavsourceInfo>(LavsourceInfo::class.java) {

    private val installedAppPackageNameList by lazy {
        @Suppress("DEPRECATION")
        GlobalComponents.application.packageManager.getInstalledApplications(0).map {
            it.packageName
        }
    }

    fun listEnabled(): List<LavsourceInfo> = run {
        query {
            where().eq("enabled", true)
        }.filter {
            installedAppPackageNameList.contains(it.packageName)
        }
    }
}