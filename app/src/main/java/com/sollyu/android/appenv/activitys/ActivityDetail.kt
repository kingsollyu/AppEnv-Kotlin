/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.activitys

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog.show
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.support.design.widget.Snackbar
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.elvishew.xlog.XLog
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder
import com.just.agentweb.AgentWeb
import com.just.agentweb.AgentWebConfig
import com.sollyu.android.appenv.R
import com.sollyu.android.appenv.bean.PhoneModel
import com.sollyu.android.appenv.commons.*
import com.sollyu.android.appenv.commons.Random
import com.sollyu.android.appenv.define.AppEnvConstants
import com.sollyu.android.appenv.events.EventSample
import com.squareup.okhttp.*
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_activity_detail.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.xutils.view.annotation.Event
import org.xutils.x
import java.io.IOException
import java.util.*


@Suppress("unused")
class ActivityDetail : ActivityBase() {

    companion object {
        fun launch(activity: Activity, applicationInfo: ApplicationInfo?) {
            val intent = Intent(activity, ActivityDetail::class.java)

            intent.putExtra("packageName", applicationInfo?.packageName)
            activity.startActivity(intent)
        }
    }

    /**
     * 随机对象
     */
    private val random = Random.New()

    /**
     * 上传点击清除数据的时间
     */
    private var wipeDataConfirm = false

    /**
     * 上个界面传送过来的应用信息
     */
    val appInfo: ApplicationInfo by lazy {
        val packageName = activity.intent.getStringExtra("packageName")
        return@lazy activity.packageManager.getApplicationInfo(packageName, 0)
    }

    override fun onInitView() {
        super.onInitView()
        setContentView(R.layout.activity_detail)

        x.view().inject(activity)
        EventBus.getDefault().register(this)

        setSupportActionBar(toolbar)
        supportActionBar?.title = appInfo.loadLabel(packageManager)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onInitData() {
        super.onInitData()

        this.jsonObjectToUi(SettingsXposed.Instance.get(appInfo.packageName))
    }

    override fun onInitDone() {
        super.onInitDone()
        Phones.Reload()

        val isSystemApp       = fun(applicationInfo: ApplicationInfo): Boolean { return ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) }
        val isSystemUpdateApp = fun(applicationInfo: ApplicationInfo): Boolean { return ((applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) }
        val isUserApp         = fun(applicationInfo: ApplicationInfo): Boolean { return (!isSystemApp(applicationInfo) && !isSystemUpdateApp(applicationInfo)) }

        if (isSystemApp(appInfo)) {
            Snackbar.make(fab, "⚠️您现在正在修改系统程序⚠️\n这可能会使您的手机无法正常开机。", Snackbar.LENGTH_INDEFINITE).show()
        }

        when (appInfo.packageName) {
            "com.tencent.mobileqq" -> {
                Snackbar.make(fab, "\uD83D\uDCF1手机QQ无法设置成iPhone在线，请谅解！", Snackbar.LENGTH_INDEFINITE).setAction("就要") {
                    oieBuildManufacturer.rightEditText = "iРhone"
                    oieBuildModel       .rightEditText = "X"
                    Snackbar.make(fab, "亲，您这样只是自欺欺人罢了", Snackbar.LENGTH_LONG).show()
                }.show();
            }
            "com.qzone" -> {
                Snackbar.make(fab, "⚠️如果您将机型乱写⚠️\nQQ空间会把您的机型变成小写", Snackbar.LENGTH_INDEFINITE).setAction("iPhone?") {
                    Snackbar.make(fab, "亲，您这样只是自欺欺人罢了", Snackbar.LENGTH_LONG).show()
                    oieBuildManufacturer.rightEditText = "iРhone"
                    oieBuildModel       .rightEditText = "X"
                    Snackbar.make(fab, "亲，您这样只是自欺欺人罢了", Snackbar.LENGTH_LONG).show()
                }.show()
            }
            "com.sina.weibo" -> {
                Snackbar.make(fab, "⚠️微博显示的继续有点少⚠️\n有时候修改无效可能是微博没有收录这个机型", Snackbar.LENGTH_INDEFINITE).show()
            }
            "com.sankuai.meituan" -> {
                Snackbar.make(fab, "\uD83C\uDE32请不要使用本软件恶意刷单!", Snackbar.LENGTH_INDEFINITE).show()
            }
            "me.ele" -> {
                Snackbar.make(fab, "\uD83C\uDE32请不要使用本软件恶意刷单!", Snackbar.LENGTH_INDEFINITE).show()
            }
            "com.coolapk.market" -> {
                Snackbar.make(fab, "\uD83D\uDE0F酷安基友，雷好啊～～", Snackbar.LENGTH_LONG).show()
            }
            "com.tencent.mm" -> {
                Snackbar.make(fab, "⛔警告⛔\n使用本软件用来微信养号，更容易被封！", Snackbar.LENGTH_INDEFINITE).show()
            }
            "com.tencent.tmgp.sgame" -> {
                Snackbar.make(fab, "⚠️使用本软件可以打开王者荣耀高帧率模式\n但是也有很小的几率封号，望众知。", Snackbar.LENGTH_INDEFINITE).setAction("开启") {
                    oieBuildManufacturer.rightEditText = "OPPO"
                    oieBuildModel.rightEditText = "OPPO r11 plus"
                }.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun getMobclickAgentTag(): String {
        return "Detail"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuDeleteConfig -> { this.onItemClickDeleteConfig()  }
            R.id.menuUploadConfig -> { this.onItemClickUploadConfig()  }
            R.id.menuSolutionSave -> { this.onItemClickSolutionSave()  }
            R.id.menuSolutionLoad -> { this.onItemClickSolutionLoad()  }
            R.id.menuSolutionDele -> { this.onItemClickSolutionDelete()}
            R.id.menuRemoteRandom -> { this.onItemClickRemoteRandom()  }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 收到消息事件
     */
    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = false)
    fun onEventBus(eventSample: EventSample) {
        when (eventSample.eventTYPE) {
            EventSample.TYPE.DETAIL_JSON_2_UI -> {
                XLog.d(eventSample.value)
                if (eventSample.value is JSONObject) {
                    jsonObjectToUi(eventSample.value as JSONObject)
                }
            }
            else -> {
            }
        }
    }

    /**
     *
     */
    @Event(R.id.menu_save_config)
    private fun onBtnClickFinish(view: View) {
        SettingsXposed.Instance.set(appInfo.packageName, uiToJsonObject())
        EventBus.getDefault().postSticky(EventSample(EventSample.TYPE.MAIN_REFRESH))
        Snackbar.make(view, R.string.detail_finish_snackbar, Snackbar.LENGTH_LONG).setAction(R.string.finish) { activity.finish() }.show()
    }

    private fun jsonObjectToUi(jsonObject: JSONObject?) {

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
        if (jsonObject?.containsKey("android.telephony.TelephonyManager.getSimCountryIso") == true)
            oieSimCountryIso.rightEditText = jsonObject.getString("android.telephony.TelephonyManager.getSimCountryIso")
        if (jsonObject?.containsKey("android.telephony.TelephonyManager.getSimOperatorName") == true)
            oieSimOperatorName.rightEditText = jsonObject.getString("android.telephony.TelephonyManager.getSimOperatorName")
        if (jsonObject?.containsKey("android.telephony.TelephonyManager.getSimSerialNumber") == true)
            oieSimSerialNumber.rightEditText = jsonObject.getString("android.telephony.TelephonyManager.getSimSerialNumber")
        if (jsonObject?.containsKey("android.telephony.TelephonyManager.getSimState") == true)
            oieSimStatus.rightEditText = jsonObject.getString("android.telephony.TelephonyManager.getSimState")

        //
        if (jsonObject?.containsKey("android.net.NetworkInfo.getType") == true)
            oiePhoneNetworkType.rightEditText = jsonObject.getString("android.net.NetworkInfo.getType")
        if (jsonObject?.containsKey("android.net.wifi.WifiInfo.getSSID") == true)
            oieWifiName.rightEditText = jsonObject.getString("android.net.wifi.WifiInfo.getSSID")
        if (jsonObject?.containsKey("android.net.wifi.WifiInfo.getBSSID") == true)
            oieWifiBssid.rightEditText = jsonObject.getString("android.net.wifi.WifiInfo.getBSSID")
        if (jsonObject?.containsKey("android.net.wifi.WifiInfo.getMacAddress") == true)
            oieWifiMacAddress.rightEditText = jsonObject.getString("android.net.wifi.WifiInfo.getMacAddress")

        //
        if (jsonObject?.containsKey("android.content.res.language") == true)
            oieLanguage.rightEditText = jsonObject.getString("android.content.res.language")
        if (jsonObject?.containsKey("android.content.res.display.dpi") == true)
            oieDisplayDpi.rightEditText = jsonObject.getString("android.content.res.display.dpi")

    }

    private fun uiToJsonObject(): JSONObject {
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
        jsonObject.put("android.telephony.TelephonyManager.getSimCountryIso"  , oieSimCountryIso.rightEditText.toString()  , true)
        jsonObject.put("android.telephony.TelephonyManager.getSimOperatorName", oieSimOperatorName.rightEditText.toString(), true)
        jsonObject.put("android.telephony.TelephonyManager.getSimSerialNumber", oieSimSerialNumber.rightEditText.toString(), true)
        jsonObject.put("android.telephony.TelephonyManager.getSimState"       , oieSimStatus.rightEditText.toString()      , true)

        jsonObject.put("android.net.NetworkInfo.getType"        , oiePhoneNetworkType.rightEditText.toString() , true)
        jsonObject.put("android.net.wifi.WifiInfo.getSSID"      , oieWifiName.rightEditText.toString()         , true)
        jsonObject.put("android.net.wifi.WifiInfo.getBSSID"     , oieWifiBssid.rightEditText.toString()        , true)
        jsonObject.put("android.net.wifi.WifiInfo.getMacAddress", oieWifiMacAddress.rightEditText.toString()   , true)

        jsonObject.put("android.content.res.language"   , oieLanguage.rightEditText.toString()  , true)
        jsonObject.put("android.content.res.display.dpi", oieDisplayDpi.rightEditText.toString(), true)
        XLog.json(jsonObject.toJSONString())

        return jsonObject
    }

    /**
     *
     */
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

    /**
     *
     */
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

    /**
     *
     */
    @Event(R.id.oieBuildSerial)
    private fun onItemClickBuildSerial(view: View) {
        val menuPop = PopupMenu(activity, view)
        menuPop.menu.add(R.string.random)
        BottomSheetBuilder(activity, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .expandOnStart(true)
                .setMenu(menuPop.menu)
                .setItemClickListener { oieBuildSerial.rightEditText = random.buildSerial() }
                .createDialog()
                .show()
    }

    /**
     *
     */
    @Event(R.id.oieBuildVersionName)
    private fun onItemClickBuildVersionName(view: View) {
        val popupMenu = PopupMenu(activity, view)
        Random.ANDROID_VERSION.values().forEach { popupMenu.menu.add(it.versionName) }
        BottomSheetBuilder(activity, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .expandOnStart(true)
                .setMenu(popupMenu.menu)
                .setItemClickListener { item -> oieBuildVersionName.rightEditText = Random.ANDROID_VERSION.get(item.title.toString()).versionName }
                .createDialog()
                .show()
    }

    /**
     *
     */
    @Event(R.id.oieAndroidId)
    private fun onItemClickAndroidId(view: View) {
        val popupMenu = PopupMenu(activity, view)
        popupMenu.menu.add(R.string.random)
        BottomSheetBuilder(activity, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .expandOnStart(true)
                .setMenu(popupMenu.menu)
                .setItemClickListener { oieAndroidId.rightEditText = random.androidId() }
                .createDialog()
                .show()
    }

    /**
     *
     */
    @Event(R.id.oieSimLine1Number)
    private fun onItemClickSimLine1Number(view: View) {
        val popupMenu = PopupMenu(activity, view)
        popupMenu.menu.add(R.string.random)
        BottomSheetBuilder(activity, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .expandOnStart(true)
                .setMenu(popupMenu.menu)
                .setItemClickListener { oieSimLine1Number.rightEditText = random.simLine1Number() }
                .createDialog()
                .show()
    }

    @Event(R.id.oieSimGetDeviceId)
    private fun onItemClickSimGetDeviceId(view: View) {
        val popupMenu = PopupMenu(activity, view)
        popupMenu.menu.add(R.string.random)
        BottomSheetBuilder(activity, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .expandOnStart(true)
                .setMenu(popupMenu.menu)
                .setItemClickListener { oieSimGetDeviceId.rightEditText = random.simGetDeviceId() }
                .createDialog()
                .show()
    }

    @Event(R.id.oieSimCountryIso)
    private fun onItemClickSimCountryIso(view: View) {
        val popupMenu = PopupMenu(activity, view)
        Random.SIM_COUNTRY_ISO.values().forEach { popupMenu.menu.add(it.label) }
        BottomSheetBuilder(activity, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .expandOnStart(true)
                .setMenu(popupMenu.menu)
                .setItemClickListener { oieSimCountryIso.rightEditText = Random.SIM_COUNTRY_ISO.get(it.title.toString()).code }
                .createDialog()
                .show()
    }

    @Event(R.id.oieSimOperator, R.id.oieSimOperatorName, R.id.oieSimSubscriberId, R.id.oieSimSerialNumber)
    private fun onItemClickSimOperator(view: View) {
        val popupMenu = PopupMenu(activity, view)
        Random.SIM_TYPE.values().forEach { popupMenu.menu.add(it.label) }
        BottomSheetBuilder(activity, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .expandOnStart(true)
                .setMenu(popupMenu.menu)
                .setItemClickListener { item ->
                    oieSimSerialNumber.rightEditText = random.simSerialNumber(Random.SIM_TYPE.get(item.title.toString()))
                    oieSimSubscriberId.rightEditText = random.simSubscriberId(Random.SIM_TYPE.get(item.title.toString()))
                    oieSimOperator    .rightEditText = Random.SIM_TYPE.get(item.title.toString()).simCode
                    oieSimCountryIso  .rightEditText = Random.SIM_TYPE.get(item.title.toString()).simCountryIso
                    oieSimOperatorName.rightEditText = Random.SIM_TYPE.get(item.title.toString()).label
                    oieSimStatus      .rightEditText = TelephonyManager.SIM_STATE_READY.toString()
                }
                .createDialog()
                .show()
    }

    @Event(R.id.oiePhoneNetworkType)
    private fun onItemClickPhoneNetworkType(view: View) {
        val popupMenu = PopupMenu(activity, view)
        Random.NETWORK_TYPE.values().forEach { popupMenu.menu.add(it.label) }
        BottomSheetBuilder(activity, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .expandOnStart(true)
                .setMenu(popupMenu.menu)
                .setItemClickListener { item ->
                    oiePhoneNetworkType.rightEditText = Random.NETWORK_TYPE.get(item.title.toString()).code
                }
                .createDialog()
                .show()
    }


    @Event(R.id.oieWifiName)
    private fun onItemClickWifiName(view: View) {
        val popupMenu = PopupMenu(activity, view)
        popupMenu.menu.add(R.string.random)
        BottomSheetBuilder(activity, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .expandOnStart(true)
                .setMenu(popupMenu.menu)
                .setItemClickListener { oieWifiName.rightEditText = random.wifiName() }
                .createDialog()
                .show()
    }

    @Event(R.id.oieWifiMacAddress)
    private fun onItemClickWifiMacAddress(view: View) {
        val popupMenu = PopupMenu(activity, view)
        popupMenu.menu.add(R.string.random)
        BottomSheetBuilder(activity, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .expandOnStart(true)
                .setMenu(popupMenu.menu)
                .setItemClickListener { oieWifiMacAddress.rightEditText = random.wifiMacAddress() }
                .createDialog()
                .show()
    }

    @Event(R.id.oieWifiBssid)
    private fun onItemClickWifiBssid(view: View) {
        val popupMenu = PopupMenu(activity, view)
        popupMenu.menu.add(R.string.random)
        BottomSheetBuilder(activity, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .expandOnStart(true)
                .setMenu(popupMenu.menu)
                .setItemClickListener { oieWifiBssid.rightEditText = random.wifiMacAddress() }
                .createDialog()
                .show()
    }

    @Event(R.id.oieLanguage)
    private fun onItemClickLanguage(view: View) {
        val popupMenu = PopupMenu(activity, view)
        Random.LANGUAGES.values().forEach { popupMenu.menu.add(it.label) }
        BottomSheetBuilder(activity, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .expandOnStart(true)
                .setMenu(popupMenu.menu)
                .setItemClickListener { oieLanguage.rightEditText = Random.LANGUAGES.get(it.title.toString()).code }
                .createDialog()
                .show()
    }

    @Event(R.id.oieDisplayDpi)
    private fun onItemClickDisplayDpi(@Suppress("UNUSED_PARAMETER") view: View) {
        Snackbar.make(fab, "考虑手机屏幕尺寸不同，DPI不提供随机功能，请手动输入数字", Snackbar.LENGTH_LONG).show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Event(R.id.menu_random_all)
    private fun onItemClickRandomAll(view: View) {
        this.jsonObjectToUi(Random.New().randomAll())
    }

    @Event(R.id.menu_run_app)
    private fun onItemClickRunApp(view: View) {
        if (Settings.Instance.isUseRoot) {
            val launchIntent = packageManager.getLaunchIntentForPackage(appInfo.packageName)
            if (launchIntent != null) {
                startActivity(launchIntent)//null pointer check in case package name was not found
            } else {
                Snackbar.make(fab, "此程序没有界面，无法启动", Snackbar.LENGTH_LONG).show()
            }
        } else {
            Snackbar.make(view, R.string.detail_run_app, Snackbar.LENGTH_LONG)
                    .setAction(R.string.detail_run_app_back) {
                        val home = Intent(Intent.ACTION_MAIN)
                        home.addCategory(Intent.CATEGORY_HOME)
                        startActivity(home)
                    }.show()
        }
    }

    @Event(R.id.menu_force_stop)
    private fun onItemClickShowApp(view: View) {
        if (Settings.Instance.isUseRoot) {
            eu.chainfire.libsuperuser.Shell.SU.run("am force-stop " + appInfo.packageName.toLowerCase())
            Snackbar.make(fab, "强制停止执行顺利执行，但具体结果取决于root是否完成", Snackbar.LENGTH_LONG).show()
        }else{
            val intent = Intent()
            intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.parse("package:" + appInfo.packageName)
            activity.startActivity(intent)
        }
    }

    @Event(R.id.menu_clear_app)
    private fun onItemClickClearApp(view: View) {
        if (Settings.Instance.isUseRoot) {
            if (wipeDataConfirm) {
                wipeDataConfirm = false
                eu.chainfire.libsuperuser.Shell.SU.run("pm clear " + appInfo.packageName.toLowerCase())
                Snackbar.make(fab, "清空数据执行顺利执行，但具体结果取决于root是否完成", Snackbar.LENGTH_LONG).show()
            } else {
                wipeDataConfirm = true
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        if (wipeDataConfirm) {
                            Snackbar.make(fab, "清除数据为敏感操作，请在2秒内连续点击次。", Snackbar.LENGTH_LONG).show()
                        }
                        wipeDataConfirm = false
                    }
                }, 2000)
            }
        } else {
            val intent = Intent()
            intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.parse("package:" + appInfo.packageName)
            activity.startActivity(intent)
        }
    }

    /**
     *
     */
    private fun onItemClickDeleteConfig() {
        SettingsXposed.Instance.remove(appInfo.packageName)
        EventBus.getDefault().postSticky(EventSample(EventSample.TYPE.MAIN_REFRESH))
        activity.finish()
    }

    private fun onItemClickUploadConfig() {
        val cookie = AgentWebConfig.getCookiesByUrl(AppEnvConstants.URL_APPENV_SERVER)
        if (cookie.isNullOrEmpty()) {
            MaterialDialog.Builder(activity).title(R.string.tip).content("没有检测到您登陆").positiveText(android.R.string.ok).show()
            return
        }

        MaterialDialog.Builder(activity)
                .title(R.string.tip)
                .input("请输入保存方案的名称", "", false) { dialog, input ->
                    dialog.dismiss()

                    val uiConfigJsonObject = uiToJsonObject();
                    uiConfigJsonObject.put("config.name"      , input.toString())
                    uiConfigJsonObject.put("app.package.label", appInfo.loadLabel(activity.packageManager))

                    val materialDialog = MaterialDialog.Builder(activity).title(R.string.tip).content("正在上传……").progress(true, 0).cancelable(false).show()
                    val formBody = FormEncodingBuilder().add(Base64.encodeToString(appInfo.packageName.toByteArray(), Base64.NO_WRAP), uiConfigJsonObject.toJSONString()).build()
                    OkHttpClient().newCall(Request.Builder().url(AppEnvConstants.URL_APPENV_UPLOAD_PACKAGE).header("Cookie", cookie).post(formBody).build()).enqueue(object : Callback {
                        override fun onFailure(request: Request, e: IOException) {
                            materialDialog.dismiss()
                            activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("上传出现错误：\n" + Log.getStackTraceString(e)).positiveText(android.R.string.ok).show() }
                        }

                        override fun onResponse(response: Response) {
                            materialDialog.dismiss()
                            try {
                                val serverResult = response.body().string()
                                XLog.d(serverResult)
                                val jsonObject = JSON.parseObject(serverResult)
                                if (jsonObject.getInteger("ret") == 200) {
                                    activity.runOnUiThread { Snackbar.make(fab, "上传成功", Snackbar.LENGTH_LONG).show() }
                                } else {
                                    activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("上传出现错误：\n" + jsonObject.getString("msg")).positiveText(android.R.string.ok).show() }
                                }
                            } catch (throwable: Throwable) {
                                activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("上传出现错误：\n请确定您已经正确的登陆").positiveText(android.R.string.ok).show() }
                            }
                        }
                    })
                }
                .show()
    }

    /**
     *
     */
    @Event(R.id.menu_scan_qr_code)
    private fun onItemClickScanQRCode(view: View) {
        ActivityScanQR.launch(activity, ActivityScanQR.FROM_DETAIL)
    }

    /**
     *
     */
    private fun onItemClickSolutionSave() {
        MaterialDialog.Builder(activity)
                .title(R.string.detail_solution_save_title)
                .input(R.string.detail_solution_save_hint, R.string.empty, false) { _, input ->
                    Solution.Instance.set(input.toString(), uiToJsonObject())
                    Snackbar.make(fab, activity.getString(R.string.detail_solution_save_success, input), Snackbar.LENGTH_LONG).show()
                }
                .show()
    }

    private fun onItemClickSolutionLoad() {
        MaterialDialog.Builder(activity)
                .title(R.string.detail_solution_load_title)
                .items(Solution.Instance.jsonObject.keys)
                .itemsCallback { _, _, _, text -> jsonObjectToUi(Solution.Instance.get(text.toString())) }
                .show()
    }

    private fun onItemClickSolutionDelete() {
        MaterialDialog.Builder(activity)
                .title(R.string.detail_solution_delete_title)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .items(Solution.Instance.jsonObject.keys)
                .itemsCallbackMultiChoice(null) { _, _, text ->
                    text.forEach { Solution.Instance.remove(it.toString()) }
                    Snackbar.make(fab, activity.getString(R.string.detail_solution_delete_success, Arrays.toString(text)), Snackbar.LENGTH_LONG).show()
                    return@itemsCallbackMultiChoice true
                }
                .show()
    }

    private fun onItemClickRemoteRandom() {
        val cookie = AgentWebConfig.getCookiesByUrl(AppEnvConstants.URL_APPENV_SERVER)
        if (cookie.isNullOrEmpty()) {
            MaterialDialog.Builder(activity).title(R.string.tip).content("没有检测到您登陆").positiveText(android.R.string.ok).show()
            return
        }

        val materialDialog = MaterialDialog.Builder(activity).title(R.string.tip).content("正在获取数据……").progress(true, 0).cancelable(false).show()
        OkHttpClient().newCall(Request.Builder().url(AppEnvConstants.URL_APPENV_RANDOM_PACKAGE).header("Cookie", cookie).build()).enqueue(object : Callback {
            override fun onFailure(request: Request, e: IOException) {
                materialDialog.dismiss()
                activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("远程随机出现错误：\n" + Log.getStackTraceString(e)).positiveText(android.R.string.ok).show() }
            }

            override fun onResponse(response: Response) {
                materialDialog.dismiss()
                try {
                    val serverResult = response.body().string()
                    XLog.d(serverResult)
                    val jsonObject = JSON.parseObject(serverResult)
                    if (jsonObject.getInteger("ret") == 200) {
                        activity.runOnUiThread { jsonObjectToUi(jsonObject.getJSONObject("data")) }
                        activity.runOnUiThread { Snackbar.make(fab, "远程随机成功(扣除2次使用点数)", Snackbar.LENGTH_LONG).show() }
                    } else {
                        activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("远程随机出现错误：\n" + jsonObject.getString("msg")).positiveText(android.R.string.ok).show() }
                    }
                } catch (throwable: Throwable) {
                    activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("远程随机出现错误：\n请确定您已经正确的登陆").positiveText(android.R.string.ok).show() }
                }
            }
        })
    }

    private fun JSONObject.put(key: String, value: String, boolean: Boolean) {
        if (value.isEmpty() && boolean)
            this.remove(key)
        else
            this.put(key, value)
    }

}
