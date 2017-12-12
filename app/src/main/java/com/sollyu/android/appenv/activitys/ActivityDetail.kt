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
import com.sollyu.android.appenv.bean.PhoneModel
import com.sollyu.android.appenv.commons.Phones
import com.sollyu.android.appenv.commons.SettingsXposed
import com.sollyu.android.appenv.events.EventSample
import kotlinx.android.synthetic.main.content_activity_detail.*
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

        when (appInfo.packageName) {
            "com.tencent.mobileqq" -> {
                Snackbar.make(btnFinish, "\uD83D\uDCF1手机QQ无法设置成iPhone在线，请谅解！", Snackbar.LENGTH_INDEFINITE).show();
            }
            "com.sankuai.meituan" -> {
                Snackbar.make(btnFinish, "🈲请不要使用本软件恶意刷单!", Snackbar.LENGTH_INDEFINITE).show()
            }
            "me.ele" -> {
                Snackbar.make(btnFinish, "🈲请不要使用本软件恶意刷单!", Snackbar.LENGTH_INDEFINITE).show()
            }
            "com.tencent.mm" -> {
                Snackbar.make(btnFinish, "⚠️使用本软件用来微信养号，更容易被封！", Snackbar.LENGTH_INDEFINITE).show()
            }
            "com.qzone" -> {
                Snackbar.make(btnFinish, "⚠如果您将机型乱写，QQ空间会把您的机型变成小写", Snackbar.LENGTH_INDEFINITE).show()
            }
            "com.sina.weibo" -> {
                Snackbar.make(btnFinish, "⚠微博显示的继续有点少，有时候修改无效可能是微博没有收录这个机型", Snackbar.LENGTH_INDEFINITE).show()
            }
            "com.tencent.tmgp.sgame" -> {
                Snackbar.make(btnFinish, "⚠️使用本软件可以打开王者荣耀高帧率模式\n但是也有很小的几率封号，希众知。", Snackbar.LENGTH_INDEFINITE).setAction("开启") {
                    oieBuildManufacturer.rightEditText = "Xiaomi"
                    oieBuildModel.rightEditText = "MIX"
                }.show()
            }
        }
    }

    @Event(R.id.btnFinish)
    private fun onBtnClickFinish(view: View) {
        val jsonObject = JSONObject()
        jsonObject.put("android.os.Build.ro.product.manufacturer", oieBuildManufacturer.rightEditText.toString(), true)
        jsonObject.put("android.os.Build.ro.product.model"       , oieBuildModel.rightEditText.toString()       , true)
        jsonObject.put("android.os.Build.ro.serialno"            , oieBuildSerial.rightEditText.toString()      , true)
        jsonObject.put("android.os.Build.VERSION.RELEASE"        , oieBuildVersionName.rightEditText.toString() , true)

        jsonObject.put("android.os.SystemProperties.android_id", oieAndroidId.rightEditText.toString(), true)

        jsonObject.put("android.telephony.TelephonyManager.getLine1Number"    , oieSimLine1Number.rightEditText.toString() , true)
        jsonObject.put("android.telephony.TelephonyManager.getDeviceId"       , oieSimGetDeviceId.rightEditText.toString() , true)
        jsonObject.put("android.telephony.TelephonyManager.getSubscriberId"   , oieSimSubscriberId.rightEditText.toString(), true)
        jsonObject.put("android.telephony.TelephonyManager.getSimOperator"    , oieSimOperator.rightEditText.toString()    , true)
        jsonObject.put("android.telephony.TelephonyManager.getSimOperatorName", oieSimOperatorName.rightEditText.toString(), true)
        jsonObject.put("android.telephony.TelephonyManager.getSimSerialNumber", oieSimSerialNumber.rightEditText.toString(), true)
        jsonObject.put("android.telephony.TelephonyManager.getSimState"       , oieSimStatus.rightEditText.toString()      , true)

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
                .setItemClickListener { item -> oieBuildManufacturer.rightEdit.setText(Phones.Instance.phoneList[item.title]?.get(0)?.manufacturer) }
                .createDialog()
                .show()
    }

    @Event(R.id.oieBuildModel)
    private fun onItemClickBuild(view: View) {
        val menuPop = PopupMenu(activity, view)
        val menuHash = HashMap<String, PhoneModel>()
        Phones.Instance.phoneList.forEach {
            it.value.filter { oieBuildManufacturer.rightEditText.toString() == it.manufacturer }.forEach {
                menuHash.put(it.name!!, it)
                menuPop.menu.add(it.name)
            }
        }

        BottomSheetBuilder(activity, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .expandOnStart(true)
                .setMenu(menuPop.menu)
                .setItemClickListener { item ->
                    oieBuildManufacturer.rightEditText = menuHash[item.title]?.manufacturer
                    oieBuildModel.rightEditText = menuHash[item.title]?.model
                }
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
