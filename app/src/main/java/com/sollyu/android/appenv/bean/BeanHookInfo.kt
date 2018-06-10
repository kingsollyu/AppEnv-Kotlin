package com.sollyu.android.appenv.bean

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.annotation.JSONField

class BeanHookInfo {
    @JSONField(name = "android.os.Build.ro.product.manufacturer")
    var buildManufacturer:String? = null

    @JSONField(name = "android.os.Build.ro.product.model")
    var buildModel:String? = null

    @JSONField(name = "android.os.Build.ro.serialno")
    var buildSerial:String? = null

    @JSONField(name = "android.os.Build.VERSION.RELEASE")
    var buildVersionName:String? = null

    @JSONField(name = "android.os.SystemProperties.android_id")
    var androidId:String? = null

    @JSONField(name = "android.telephony.TelephonyManager.getLine1Number")
    var simLine1Number:String? = null

    @JSONField(name = "android.telephony.TelephonyManager.getDeviceId")
    var simGetDeviceId:String? = null

    @JSONField(name = "android.telephony.TelephonyManager.getSubscriberId")
    var simSubscriberId:String? = null

    @JSONField(name = "android.telephony.TelephonyManager.getSimOperator")
    var simOperator:String? = null

    @JSONField(name = "android.telephony.TelephonyManager.getSimCountryIso")
    var simCountryIso:String? = null

    @JSONField(name = "android.telephony.TelephonyManager.getSimOperatorName")
    var simOperatorName:String? = null

    @JSONField(name = "android.telephony.TelephonyManager.getSimSerialNumber")
    var simSerialNumber:String? = null

    @JSONField(name = "android.telephony.TelephonyManager.getSimState")
    var simStatus:String? = null

    @JSONField(name = "android.net.NetworkInfo.getType")
    var phoneNetworkType:String? = null

    @JSONField(name = "android.net.wifi.WifiInfo.getSSID")
    var wifiName:String? = null

    @JSONField(name = "android.net.wifi.WifiInfo.getBSSID")
    var wifiBssid:String? = null

    @JSONField(name = "android.net.wifi.WifiInfo.getMacAddress")
    var wifiMacAddress:String? = null

    @JSONField(name = "android.content.res.language")
    var language:String? = null

    @JSONField(name = "android.content.res.display.dpi")
    var displayDpi:String? = null

    @JSONField(serialize = false)
    override fun toString(): String {
        return JSON.toJSONString(this)
    }

    fun toJSON():JSONObject{
        return JSON.parseObject(toString())
    }
}