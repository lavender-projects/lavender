package de.honoka.lavender.android.data

data class LavsourceInfo(

    var id: Long? = null,

    var type: String? = null,

    var name: String? = null,

    var packageName: String? = null,

    var imgUrl: String? = null,

    var baseUrl: String? = null
) {

    object Type {

        const val LOCAL = "local"

        const val NETWORK = "network"
    }
}