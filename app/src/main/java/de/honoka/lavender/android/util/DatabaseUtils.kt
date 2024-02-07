package de.honoka.lavender.android.util

import de.honoka.lavender.android.dao.LavsourceInfoDao
import de.honoka.sdk.util.android.database.AbstractAndroidDatabaseUtils
import de.honoka.sdk.util.android.database.BaseDao

object DatabaseUtils : AbstractAndroidDatabaseUtils() {

    override val daoInstances: List<BaseDao<*>> = listOf(
        LavsourceInfoDao
    )
}