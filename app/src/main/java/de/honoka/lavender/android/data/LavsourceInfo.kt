package de.honoka.lavender.android.data

import de.honoka.lavender.android.entity.LavsourceInfo

data class LavsourceInfoVo(

    var iconUrl: String? = null
) : LavsourceInfo()

data class LavsourceAddParams(

    var iconUrl: String? = null
) : LavsourceInfo()