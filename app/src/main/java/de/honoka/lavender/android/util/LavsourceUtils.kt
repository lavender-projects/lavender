package de.honoka.lavender.android.util

import de.honoka.lavender.android.lavsource.sdk.business.stub.BasicBusinessStub

object LavsourceUtils {

    fun getLavsourceStatus(packageName: String): Boolean = run {
        BasicBusinessStub(packageName).statusCheck().getBool("status")
    }
}