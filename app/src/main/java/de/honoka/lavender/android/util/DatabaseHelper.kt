package de.honoka.lavender.android.util

import android.database.sqlite.SQLiteDatabase
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import de.honoka.sdk.util.android.common.GlobalData

abstract class DatabaseHelper(
    databaseName: String,
    private val entityClasses: List<Class<*>>
) : OrmLiteSqliteOpenHelper(GlobalData.application, databaseName, null, 1) {

    override fun onCreate(database: SQLiteDatabase, connectionSource: ConnectionSource) {
        entityClasses.forEach {
            TableUtils.createTable(connectionSource, it)
        }
    }

    override fun onUpgrade(
        database: SQLiteDatabase?,
        connectionSource: ConnectionSource?,
        oldVersion: Int,
        newVersion: Int
    ) {

    }
}

class DefaultDatabaseHelper : DatabaseHelper(
    "main.db",
    listOf()
)