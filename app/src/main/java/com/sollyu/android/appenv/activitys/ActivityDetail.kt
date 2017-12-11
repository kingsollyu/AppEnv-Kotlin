package com.sollyu.android.appenv.activitys

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.PopupMenu
import com.alibaba.fastjson.JSONObject
import com.elvishew.xlog.XLog
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder
import com.sollyu.android.appenv.R
import com.sollyu.android.appenv.commons.Phones
import com.sollyu.android.appenv.commons.SettingsXposed
import com.sollyu.android.appenv.events.EventSample
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.greenrobot.eventbus.EventBus
import org.xutils.view.annotation.Event
import org.xutils.x

@Suppress("unused")
class ActivityDetail : ActivityBase() {

    companion object {
        fun launch(activity: Activity, applicationInfo: ApplicationInfo?) {
            val intent = Intent(activity, ActivityDetail::class.java)

            intent.putExtra("packageName", applicationInfo?.packageName)
            activity.startActivity(intent)
        }
    }

    val appInfo: ApplicationInfo by lazy {
        val packageName = activity.intent.getStringExtra("packageName")
        return@lazy activity.packageManager.getApplicationInfo(packageName, 0)
    }

    override fun onInitView() {
        super.onInitView()
        setContentView(R.layout.activity_detail)

        x.view().inject(activity)

        setSupportActionBar(toolbar)
        supportActionBar?.title = appInfo.loadLabel(packageManager)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onInitData() {
        super.onInitData()

        val jsonObject = SettingsXposed.Instance.get(appInfo.packageName)

        //
        if (jsonObject?.containsKey("android.os.Build.ro.product.manufacturer") == true)
            oieBuildManufacturer.rightEditText = jsonObject.getString("android.os.Build.ro.product.manufacturer")
        if (jsonObject?.containsKey("android.os.Build.ro.product.model") == true)
            oieBuildModel.rightEditText = jsonObject.getString("android.os.Build.ro.product.model")
        if (jsonObject?.containsKey("android.os.Build.ro.serialno") == true)
            oieBuildSerial.rightEditText = jsonObject.getString("android.os.Build.ro.serialno")
        if (jsonObject?.containsKey("android.os.Build.VERSION.RELEASE") == true)
            oieBuildVersionName.rightEditText = jsonObject.getString("android.os.Build.VERSION.RELEASE")

        //
        if (jsonObject?.containsKey("android.os.SystemProperties.android_id") == true)
            oieAndroidId.rightEditText = jsonObject.getString("android.os.SystemProperties.android_id")

        //
        if (jsonObject?.containsKey("android.telephony.TelephonyManager.getLine1Number") == true)
            oieSimLine1Number.rightEditText = jsonObject.getString("android.telephony.TelephonyManager.getLine1Number")
        if (jsonObject?.containsKey("android.telephony.TelephonyManager.getDeviceId") == true)
            oieSimGetDeviceId.rightEditText = jsonObject.getString("android.telephony.TelephonyManager.getDeviceId")
        if (jsonObject?.containsKey("android.telephony.TelephonyManager.getSubscriberId") == true)
            oieSimSubscriberId.rightEditText = jsonObject.getString("android.telephony.TelephonyManager.getSubscriberId")
        if (jsonObject?.containsKey("android.telephony.TelephonyManager.getSimOperator") == true)
            oieSimOperator.rightEditText = jsonObject.getString("android.telephony.TelephonyManager.getSimOperator")
        if (jsonObject?.containsKey("android.telephony.TelephonyManager.getSimOperatorName") == true)
            oieSimOperatorName.rightEditText = jsonObject.getString("android.telephony.TelephonyManager.getSimOperatorName")
        if (jsonObject?.containsKey("android.telephony.TelephonyManager.getSimSerialNumber") == true)
            oieSimSerialNumber.rightEditText = jsonObject.getString("android.telephony.TelephonyManager.getSimSerialNumber")
        if (jsonObject?.containsKey("android.telephony.TelephonyManager.getSimState") == true)
            oieSimStatus.rightEditText = jsonObject.getString("android.telephony.TelephonyManager.getSimState")

        //
        if (jsonObject?.containsKey("android.net.wifi.WifiInfo.getSSID") == true)
            oieWifiName.rightEditText = jsonObject.getString("android.net.wifi.WifiInfo.getSSID")
        if (jsonObject?.containsKey("android.net.wifi.WifiInfo.getBSSID") == true)
            oieWifiBssid.rightEditText = jsonObject.getString("android.net.wifi.WifiInfo.getBSSID")
        if (jsonObject?.containsKey("android.net.wifi.WifiInfo.getMacAddress") == true)
            oieWifiMacAddress.rightEditText = jsonObject.getString("android.net.wifi.WifiInfo.getMacAddress")

    }

    override fun onInitDone() {
        super.onInitDone()
        Phones.Reload()
    }

    @Event(R.id.btnFinish)
    private fun onBtnClickFinish(view: View) {
        val jsonObject = JSONObject()
        jsonObject.put("android.os.Build.ro.product.manufacturer", oieBuildManufacturer.rightEditText.toString(), true)
        jsonObject.put("android.os.Build.ro.product.model"       , oieBuildModel.rightEditText.toString()       , true)
        jsonObject.put("android.os.Build.ro.serialno"            , oieBuildSerial.rightEditText.toString()      , true)
        jsonObject.put("android.os.Build.VERSION.RELEASE"        , oieBuildVersionName.rightEditText.toString() , true)

        jsonObject.put("android.os.SystemProperties.android_id", oieAndroidId.rightEditText.toString(), true)

        jsonObject.put("android.telephony.TelephonyManager.getLine1Number"    , oieSimLine1Number.rightEditText.toString(), true)
        jsonObject.put("android.telephony.TelephonyManager.getDeviceId"       , oieSimGetDeviceId.rightEditText.toString(), true)
        jsonObject.put("android.telephony.TelephonyManager.getSubscriberId"   , oieSimSubscriberId.rightEditText.toString(), true)
        jsonObject.put("android.telephony.TelephonyManager.getSimOperator"    , oieSimOperator.rightEditText.toString(), true)
        jsonObject.put("android.telephony.TelephonyManager.getSimOperatorName", oieSimOperatorName.rightEditText.toString(), true)
        jsonObject.put("android.telephony.TelephonyManager.getSimSerialNumber", oieSimSerialNumber.rightEditText.toString(), true)
        jsonObject.put("android.telephony.TelephonyManager.getSimState"       , oieSimStatus.rightEditText.toString(), true)

        jsonObject.put("android.net.wifi.WifiInfo.getSSID"      , oieWifiName.rightEditText.toString()      , true)
        jsonObject.put("android.net.wifi.WifiInfo.getBSSID"     , oieWifiBssid.rightEditText.toString()     , true)
        jsonObject.put("android.net.wifi.WifiInfo.getMacAddress", oieWifiMacAddress.rightEditText.toString(), true)

        XLog.json(jsonObject.toJSONString())
        SettingsXposed.Instance.set(appInfo.packageName, jsonObject)
        EventBus.getDefault().postSticky(EventSample(EventSample.TYPE.MAIN_REFRESH))
        Snackbar.make(view, R.string.detail_finish_snackbar, Snackbar.LENGTH_LONG).setAction(R.string.finish) { activity.finish() }.show()
    }

    @Event(R.id.oieBuildManufacturer)
    private fun onItemClickBuildManufacturer(view: View) {

        val menuPop = PopupMenu(activity, view)

        Phones.Instance.phoneList.keys.forEach { menuPop.menu.add(it) }

        BottomSheetBuilder(activity, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .expandOnStart(true)
                .setMenu(menuPop.menu)
                .createDialog()
                .show()
    }

    private fun JSONObject.put(key: String, value: String, boolean: Boolean) {
        if (value.isEmpty() && boolean)
            this.remove(key)
        else
            this.put(key, value)
    }

}
