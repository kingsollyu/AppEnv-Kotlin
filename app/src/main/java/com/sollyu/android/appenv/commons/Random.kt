/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.commons

import android.os.Build
import android.telephony.TelephonyManager
import com.alibaba.fastjson.JSONObject
import com.sollyu.android.appenv.commons.libs.IMEIGen
import com.sollyu.android.appenv.commons.libs.RandomMac
import org.apache.commons.lang3.RandomUtils
import org.apache.commons.text.CharacterPredicates
import org.apache.commons.text.RandomStringGenerator

/**
 * 作者：sollyu
 * 时间：2017/12/12
 * 说明：
 */
class Random {

    companion object {
        /**
         * 创建一个新的随机对象
         */
        fun New(): Random {
            return Random()
        }
    }

    enum class ANDROID_VERSION(val versionName: String, val versionCode: Int) {
        ICE_CREAM_SANDWICH_MR1("4.0.3", Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)  ,
        JELLY_BEAN_MR1        ("4.2"  , Build.VERSION_CODES.JELLY_BEAN_MR1)          ,
        KITKAT                ("4.4"  , Build.VERSION_CODES.KITKAT)                  ,
        KITKAT_WATCH          ("4.4W" , Build.VERSION_CODES.KITKAT_WATCH)            ,
        LOLLIPOP_MR1          ("5.0"  , Build.VERSION_CODES.LOLLIPOP_MR1)            ,
        M                     ("6.0"  , Build.VERSION_CODES.M)                       ,
        N                     ("7.0"  , Build.VERSION_CODES.N)                       ,
        ;

        companion object {
            fun get(versionName: String): ANDROID_VERSION = ANDROID_VERSION.values().first { it.versionName == versionName }
        }
    }

    /**
     * Sim卡类型
     */
    enum class SIM_TYPE(val label: String, val simCode: String, val simIccid: String, val simCountryIso: String) {
        CMCC("中国移动", "46000", "898600", SIM_COUNTRY_ISO.CN.code),
        CUCC("中国联通", "46001", "898601", SIM_COUNTRY_ISO.CN.code),
        CTCC("中国电信", "46003", "898603", SIM_COUNTRY_ISO.CN.code);

        companion object {
            fun get(label: String): SIM_TYPE = SIM_TYPE.values().first { it.label == label }
        }
    }

    /**
     * sim卡国家
     */
    enum class SIM_COUNTRY_ISO(val label: String, val code: String) {
        CN("中国", "cn"),
        EN("美国", "en");

        companion object {
            fun get(label: String): SIM_COUNTRY_ISO = SIM_COUNTRY_ISO.values().first { it.label == label }
        }
    }

    /**
     * 国家语言
     */
    enum class LANGUAGES(val label: String, val code: String) {
        CN("中国_简体", "zh_CN"),
        TW("中国_繁体", "zh_TW"),
        EN("美国", "en_US"),
        JP("日本", "ja_JP");

        companion object {
            fun get(label: String): LANGUAGES = LANGUAGES.values().first { it.label == label }
        }
    }

    /**
     * 网络状态
     */
    enum class NETWORK_TYPE(val label: String, val code: String) {
        _2G("2G", TelephonyManager.NETWORK_TYPE_GPRS.toString()),
        _3G("3G", TelephonyManager.NETWORK_TYPE_UMTS.toString()),
        _4G("4G", TelephonyManager.NETWORK_TYPE_LTE.toString()),
        WIFI("WIFI", "wifi");

        companion object {
            fun get(label: String): NETWORK_TYPE = NETWORK_TYPE.values().first { it.label == label }
        }
    }


    private val simType = SIM_TYPE.values()[RandomUtils.nextInt(0, SIM_TYPE.values().size)]
    private val androidVersion = ANDROID_VERSION.values()[RandomUtils.nextInt(0, ANDROID_VERSION.values().size)]
    private val networkType = NETWORK_TYPE.values()[RandomUtils.nextInt(0, NETWORK_TYPE.values().size)]

    init {

    }

    /**
     *
     */
    fun buildSerial(): String {
        return RandomStringGenerator.Builder().withinRange('0'.toInt(), 'Z'.toInt()).filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS).build().generate(RandomUtils.nextInt(6, 8))
    }

    fun buildVersion(): String {
        return androidVersion.versionName
    }

    /**
     *
     */
    fun androidId(): String {
        return RandomStringGenerator.Builder().withinRange('0'.toInt(), 'z'.toInt()).filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS).build().generate(RandomUtils.nextInt(10, 13))
    }

    /**
     *
     */
    fun simLine1Number(): String {
        val telFirst = arrayOf("134", "135", "136", "137", "138", "139", "150", "151", "152", "157", "158", "159", "130", "131", "132", "155", "156", "133", "153")
        var line1Number = ""

        val isUserArea = RandomUtils.nextInt(0, 100) < 30
        if (isUserArea) line1Number += "+86"

        return line1Number + telFirst[RandomUtils.nextInt(0, telFirst.size)] + RandomStringGenerator.Builder().withinRange('0'.toInt(), '9'.toInt()).filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS).build().generate(8)
    }

    /**
     *
     */
    fun simGetDeviceId(): String {
        val simDeviceId = "86" + RandomStringGenerator.Builder().withinRange('0'.toInt(), '9'.toInt()).build().generate(12)
        return simDeviceId + IMEIGen.genCode(simDeviceId)
    }

    /**
     *
     */
    fun simSubscriberId(simType: SIM_TYPE): String {
        return simType.simCode + RandomStringGenerator.Builder().withinRange('0'.toInt(), '9'.toInt()).build().generate(10)
    }

    /**
     *
     */
    fun simOperator(simType: SIM_TYPE):String {
        return simType.simCode
    }

    fun simCountryIso(simType: SIM_TYPE):String {
        return simType.simCountryIso
    }

    fun simOperatorName(simType: SIM_TYPE): String {
        return simType.label
    }

    fun simSimState(simType: SIM_TYPE): String {
        return TelephonyManager.SIM_STATE_READY.toString()
    }

    fun simSerialNumber(simType: SIM_TYPE): String {
        return simType.simIccid + RandomStringGenerator.Builder().withinRange('0'.toInt(), '9'.toInt()).build().generate(14)
    }

    fun networkType(): String {
        return networkType.code
    }

    fun wifiName(): String {
        val strings = arrayOf("TP-", "FAST_", "Tenda_", "TP-LINK_", "MERCURY_")
        return strings[RandomUtils.nextInt(0, strings.size-1)] + RandomStringGenerator.Builder().withinRange('0'.toInt(), 'Z'.toInt()).filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS).build().generate(RandomUtils.nextInt(6, 8))
    }

    fun wifiMacAddress(): String {
        return RandomMac.getMacAddrWithFormat(":")
    }

    fun randomAll(): JSONObject {
        val buildManufacturerList   = Phones.Instance.phoneList.keys.toList()
        val buildManufacturerRandom = buildManufacturerList[RandomUtils.nextInt(0, buildManufacturerList.size)]
        val buildModelList          = Phones.Instance.phoneList[buildManufacturerRandom]!!
        val buildPhoneRandom        = buildModelList[RandomUtils.nextInt(0, buildModelList.size)]
        val randomJsonObject        = JSONObject()

        randomJsonObject.put("android.os.Build.ro.product.manufacturer", buildPhoneRandom.manufacturer)
        randomJsonObject.put("android.os.Build.ro.product.model", buildPhoneRandom.model)
        randomJsonObject.put("android.os.Build.ro.serialno", this.buildSerial())
        randomJsonObject.put("android.os.Build.VERSION.RELEASE", this.buildVersion())

        randomJsonObject.put("android.os.SystemProperties.android_id", this.androidId())

        randomJsonObject.put("android.telephony.TelephonyManager.getLine1Number", this.simLine1Number())
        randomJsonObject.put("android.telephony.TelephonyManager.getDeviceId", this.simGetDeviceId())
        randomJsonObject.put("android.telephony.TelephonyManager.getSubscriberId", this.simSubscriberId(simType))
        randomJsonObject.put("android.telephony.TelephonyManager.getSimOperator", this.simOperator(simType))
        randomJsonObject.put("android.telephony.TelephonyManager.getSimCountryIso", this.simCountryIso(simType))
        randomJsonObject.put("android.telephony.TelephonyManager.getSimOperatorName", this.simOperatorName(simType))
        randomJsonObject.put("android.telephony.TelephonyManager.getSimSerialNumber", this.simSerialNumber(simType))
        randomJsonObject.put("android.telephony.TelephonyManager.getSimState", this.simSimState(simType))

        randomJsonObject.put("android.net.NetworkInfo.getType", this.networkType())
        randomJsonObject.put("android.net.wifi.WifiInfo.getSSID", this.wifiName())
        randomJsonObject.put("android.net.wifi.WifiInfo.getBSSID", this.wifiMacAddress())
        randomJsonObject.put("android.net.wifi.WifiInfo.getMacAddress", this.wifiMacAddress())

        return randomJsonObject
    }
}