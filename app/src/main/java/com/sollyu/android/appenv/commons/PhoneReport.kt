/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.commons

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import com.alibaba.fastjson.JSON
import com.elvishew.xlog.XLog
import com.sollyu.android.appenv.define.AppEnvConstants
import com.squareup.okhttp.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException

/**
 * 作者：sollyu
 * 时间：2017/12/28
 * 说明：
 */
class PhoneReport {
    companion object {
        var Instance = PhoneReport()
    }

    @SuppressLint("WifiManagerLeak")
    fun start() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Application.Instance)
        if (sharedPreferences.getBoolean("report_phone", false)) {
            return
        }

        val umengFile = File(Application.Instance.filesDir, ".umeng/exchangeIdentity.json")
        if (!umengFile.exists() || !umengFile.canRead()) {
            return
        }

        val umengJson = JSON.parseObject(FileUtils.readFileToString(umengFile, "UTF-8"))
        if (umengJson.getString("umid") == null || umengJson.getString("umid").isEmpty()) {
            return
        }

        val postBody = FormEncodingBuilder()
        postBody.add("android.umeng.umid"                      , umengJson.getString("umid"))
        postBody.add("android.os.Build.ro.product.manufacturer", Build.MANUFACTURER)
        postBody.add("android.os.Build.ro.product.model"       , Build.MODEL)
        postBody.add("android.os.Build.ro.product.fingerprint" , Build.FINGERPRINT)
        postBody.add("android.os.Build.ro.serialno"            , Build.SERIAL)
        postBody.add("android.os.Build.VERSION.RELEASE"        , Build.VERSION.RELEASE)

        arrayListOf("getLine1Number", "getDeviceId", "getSubscriberId", "getSimOperator", "getSimCountryIso", "getSimOperatorName", "getSimSerialNumber", "getSimState").forEach {
            try {
                val telephonyManager = Application.Instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                postBody.add("android.telephony.TelephonyManager.$it", TelephonyManager::class.java.getMethod(it).invoke(telephonyManager).toString())
            } catch (t: Throwable) { }
        }

        arrayListOf("getSSID", "getBSSID", "getMacAddress").forEach {
            try {
                val wifiManager = Application.Instance.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val connectionInfo = wifiManager.connectionInfo
                postBody.add("android.net.wifi.WifiInfo.$it", WifiInfo::class.java.getMethod(it).invoke(connectionInfo).toString())
            } catch (t: Throwable) { }
        }

        OkHttpClient().newCall(Request.Builder().url(AppEnvConstants.URL_APPENV_REPORT_PHONE).post(postBody.build()).build()).enqueue(object : Callback{
            override fun onFailure(request: Request, e: IOException) { }

            override fun onResponse(response: Response) {
                val serverResult = response.body().string()
                try {
                    val resultJsonObject = JSON.parseObject(serverResult)
                    if (resultJsonObject.getInteger("ret") == 200) {
                        sharedPreferences.edit().putBoolean("report_phone", true).apply()
                    }
                } catch (e: Exception) { }
            }
        })
    }

}