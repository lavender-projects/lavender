package de.honoka.lavender.android.api.entity

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import de.honoka.sdk.util.android.database.Table
import java.io.Serializable

@Table(version = 1)
@DatabaseTable(tableName = "lavsource_info")
open class LavsourceInfo(

    @DatabaseField(id = true)
    var id: Long? = null,

    @DatabaseField
    var type: String? = null,

    @DatabaseField
    var name: String? = null,

    @DatabaseField
    var packageName: String? = null,

    @DatabaseField
    var baseUrl: String? = null
) : Serializable {

    object Type {

        const val LOCAL = "local"

        const val NETWORK = "network"
    }
}