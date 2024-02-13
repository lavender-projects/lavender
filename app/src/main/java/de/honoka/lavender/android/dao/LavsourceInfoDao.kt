package de.honoka.lavender.android.dao

import de.honoka.lavender.android.entity.LavsourceInfo
import de.honoka.sdk.util.android.database.BaseDao

object LavsourceInfoDao : BaseDao<LavsourceInfo>(LavsourceInfo::class.java) {

    fun listEnabled(): List<LavsourceInfo> = query {
        where().eq("enabled", true)
    }
}