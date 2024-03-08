package de.honoka.lavender.android.util

import cn.hutool.json.JSONObject
import de.honoka.lavender.android.dao.LavsourceInfoDao
import de.honoka.lavender.android.lavsource.sdk.business.stub.BasicBusinessStub

object LavsourceUtils {

    fun getLavsourceStatus(packageName: String): Boolean = run {
        BasicBusinessStub(packageName).statusCheck().getBool("status")
    }

    fun getPackageNameById(params: JSONObject, idKey: String = "lavsourceId") = run {
        LavsourceInfoDao.getCacheById(params[idKey])!!.packageName!!
    }
}