package de.honoka.lavender.android.api.data

import de.honoka.lavender.android.api.entity.LavsourceInfo

data class LavsourceInfoVo(

    var iconUrl: String? = null
) : LavsourceInfo()

data class LavsourceAddParams(

    var iconUrl: String? = null
) : LavsourceInfo()