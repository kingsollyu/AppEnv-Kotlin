/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.commons

import android.net.wifi.WifiInfo
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import com.sollyu.android.appenv.BuildConfig
import com.sollyu.android.not.proguard.NotProguard
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.apache.commons.io.FileUtils
import org.json.JSONObject
import java.io.File

/**
 * 作者：sollyu
 * 时间：2017/12/7
 * 说明：Xposed 加载类
 */
@NotProguard
class XposedEntry : IXposedHookLoadPackage {

    val TAG = "Xposed"

    /**
     * 当应用启动时
     */
    override fun handleLoadPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {

        val ignoreApplication = arrayListOf("android", "de.robv.android.xposed.installer")
        if (ignoreApplication.contains(loadPackageParam.packageName)) {
            return
        }

        /* 设置状态 */
        if (loadPackageParam.packageName == BuildConfig.APPLICATION_ID) {
            XposedBridge.hookAllMethods(XposedHelpers.findClass("com.sollyu.android.appenv.commons.Application", loadPackageParam.classLoader), "isXposedWork", MethodHookValue(true))
            return
        }

        val xposedSettingsFile = File("/sdcard/Android/data/" + BuildConfig.APPLICATION_ID + "/files/appenv.xposed.json")
        if (xposedSettingsFile.exists() && xposedSettingsFile.canRead()) {

            /* 其他包 */
            val xposedSettingsJson = JSONObject(FileUtils.readFileToString(xposedSettingsFile, "UTF-8"))
            if (xposedSettingsJson.has(loadPackageParam.packageName)) {
                val xposedPackageJson = xposedSettingsJson.getJSONObject(loadPackageParam.packageName)
                val buildValueHashMap = HashMap<String, Any>()
                if (xposedPackageJson.has("android.os.Build.ro.product.manufacturer")) {
                    val jsonValue = xposedPackageJson.getString("android.os.Build.ro.product.manufacturer")
                    XposedHelpers.setStaticObjectField(Build::class.java, "MANUFACTURER", jsonValue)
                    XposedHelpers.setStaticObjectField(Build::class.java, "PRODUCT"     , jsonValue)
                    XposedHelpers.setStaticObjectField(Build::class.java, "BRAND"       , jsonValue)
                    buildValueHashMap.put("ro.product.manufacturer", jsonValue)
                    buildValueHashMap.put("ro.product.brand"       , jsonValue)
                    buildValueHashMap.put("ro.product.name"        , jsonValue)
                }
                if (xposedPackageJson.has("android.os.Build.ro.product.model")) {
                    val jsonValue = xposedPackageJson.getString("android.os.Build.ro.product.model")
                    XposedHelpers.setStaticObjectField(Build::class.java, "MODEL" , jsonValue)
                    XposedHelpers.setStaticObjectField(Build::class.java, "DEVICE", jsonValue)
                    buildValueHashMap.put("ro.product.device", jsonValue)
                    buildValueHashMap.put("ro.product.model" , jsonValue)
                }
                if (xposedPackageJson.has("android.os.Build.ro.serialno")) {
                    XposedHelpers.setStaticObjectField(Build::class.java, "SERIAL", xposedPackageJson.getString("android.os.Build.ro.serialno"))
                    buildValueHashMap.put("ro.serialno", xposedPackageJson.getString("android.os.Build.ro.serialno"))
                }
                if (xposedPackageJson.has("android.os.Build.VERSION.RELEASE")) {
                    XposedHelpers.setStaticObjectField(Build.VERSION::class.java, "RELEASE", xposedPackageJson.getString("android.os.Build.VERSION.RELEASE"))
                }
                XposedBridge.hookAllMethods(XposedHelpers.findClass("android.os.SystemProperties", loadPackageParam.classLoader), "get", object : XC_MethodHook() {
                    override fun afterHookedMethod(methodHookParam: MethodHookParam) {
                        if (buildValueHashMap.containsKey(methodHookParam.args[0].toString())) {
                            methodHookParam.result = buildValueHashMap[methodHookParam.args[0].toString()]
                        }
                    }
                })

                if (xposedPackageJson.has("android.os.SystemProperties.android_id")) {
                    XposedBridge.hookAllMethods(android.provider.Settings.System::class.java, "getString", object : XC_MethodHook() {
                        override fun afterHookedMethod(methodHookParam: MethodHookParam) {
                            if (methodHookParam.args.size > 1 && methodHookParam.args[1].toString() == "android_id") {
                                methodHookParam.result = xposedPackageJson.getString("android.os.SystemProperties.android_id")
                            }
                        }
                    })
                }

                if (xposedPackageJson.has("android.telephony.TelephonyManager.getLine1Number")) {
                    XposedBridge.hookAllMethods(TelephonyManager::class.java, "getLine1Number", MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getLine1Number")))
                }
                if (xposedPackageJson.has("android.telephony.TelephonyManager.getDeviceId")) {
                    XposedBridge.hookAllMethods(TelephonyManager::class.java, "getDeviceId", MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getDeviceId")))
                }
                if (xposedPackageJson.has("android.telephony.TelephonyManager.getSubscriberId")) {
                    XposedBridge.hookAllMethods(TelephonyManager::class.java, "getSubscriberId", MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getSubscriberId")))
                }
                if (xposedPackageJson.has("android.telephony.TelephonyManager.getSimOperator")) {
                    XposedBridge.hookAllMethods(TelephonyManager::class.java, "getSimOperator", MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getSimOperator")))
                }
                if (xposedPackageJson.has("android.telephony.TelephonyManager.getSimCountryIso")) {
                    XposedBridge.hookAllMethods(TelephonyManager::class.java, "getSimCountryIso", MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getSimCountryIso")))
                }
                if (xposedPackageJson.has("android.telephony.TelephonyManager.getSimOperatorName")) {
                    XposedBridge.hookAllMethods(TelephonyManager::class.java, "getSimOperatorName", MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getSimOperatorName")))
                }
                if (xposedPackageJson.has("android.telephony.TelephonyManager.getSimSerialNumber")) {
                    XposedBridge.hookAllMethods(TelephonyManager::class.java, "getSimSerialNumber", MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getSimSerialNumber")))
                }
                if (xposedPackageJson.has("android.telephony.TelephonyManager.getSimState")) {
                    XposedBridge.hookAllMethods(TelephonyManager::class.java, "getSimState", MethodHookValue(xposedPackageJson.getInt("android.telephony.TelephonyManager.getSimState")))
                }

                if (xposedPackageJson.has("android.net.wifi.WifiInfo.getSSID")) {
                    XposedBridge.hookAllMethods(WifiInfo::class.java, "getSSID", MethodHookValue(xposedPackageJson.getString("android.net.wifi.WifiInfo.getSSID")))
                }
                if (xposedPackageJson.has("android.net.wifi.WifiInfo.getBSSID")) {
                    XposedBridge.hookAllMethods(WifiInfo::class.java, "getBSSID", MethodHookValue(xposedPackageJson.getString("android.net.wifi.WifiInfo.getBSSID")))
                }
                if (xposedPackageJson.has("android.net.wifi.WifiInfo.getMacAddress")) {
                    XposedBridge.hookAllMethods(WifiInfo::class.java, "getMacAddress", MethodHookValue(xposedPackageJson.getString("android.net.wifi.WifiInfo.getMacAddress")))
                }
            }
        }else{
            Log.d(TAG, String.format("[%20s]%s", "no config file", loadPackageParam.packageName))
        }
    }

    /**
     *
     */
    inner class MethodHookValue(private val value: Any) : XC_MethodHook() {
        override fun afterHookedMethod(methodHookParam: MethodHookParam) {
            methodHookParam.result = value
        }
    }

}