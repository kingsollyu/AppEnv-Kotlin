/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.activitys

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatDelegate
import android.view.View
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSON
import com.sollyu.android.appenv.BuildConfig
import com.sollyu.android.appenv.R
import com.sollyu.android.appenv.commons.Phones
import com.sollyu.android.appenv.commons.Settings
import com.sollyu.android.appenv.commons.SettingsXposed
import com.sollyu.android.appenv.events.EventSample
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import de.psdev.licensesdialog.LicensesDialog
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20
import de.psdev.licensesdialog.licenses.MITLicense
import de.psdev.licensesdialog.model.Notice
import de.psdev.licensesdialog.model.Notices
import eu.chainfire.libsuperuser.Shell
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.apache.commons.io.FileUtils
import org.greenrobot.eventbus.EventBus
import org.xutils.view.annotation.Event
import org.xutils.x
import java.io.File
import java.io.IOException

@Suppress("unused")
class ActivitySettings : ActivityBase() {

    companion object {
        fun launch(activity: Activity) {
            activity.startActivity(Intent(activity, ActivitySettings::class.java))
        }
    }

    override fun onInitView() {
        super.onInitView()
        setContentView(R.layout.activity_settings)

        x.view().inject(activity)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.settings)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onInitData() {
        super.onInitData()
        val dataLocalTmpConfigFile = File("/data/local/tmp/appenv.xposed.json")
        if (dataLocalTmpConfigFile.exists() && dataLocalTmpConfigFile.canRead() && dataLocalTmpConfigFile.canWrite())
            Settings.Instance.isUseDataLocalTmpConfig = true

        oiwShowSystemApp.setCheckedImmediatelyNoEvent(Settings.Instance.isShowSystemApp)
        oiwShowDesktopIcon.setCheckedImmediatelyNoEvent(Settings.Instance.isShowDesktopIcon)

        oiwUseRoot.setCheckedImmediatelyNoEvent(Settings.Instance.isUseRoot)
        oiwAppDataConfig.setCheckedImmediatelyNoEvent(Settings.Instance.isUseAppDataConfig)
        oiwAppDataConfig.switch.isEnabled = !Settings.Instance.isUseDataLocalTmpConfig
        oiwUseDataLocalTmp.setCheckedImmediatelyNoEvent(Settings.Instance.isUseDataLocalTmpConfig)

        oivUpdateSoftVersion.setRightText(BuildConfig.VERSION_NAME)
        oivUpdatePhoneList.setRightText(Phones.Instance.versionName)
    }

    override fun getMobclickAgentTag(): String {
        return "Settings"
    }

    @Event(R.id.oivAuthor)
    private fun onBtnClickAuthor(@Suppress("UNUSED_PARAMETER") view: View) {
        delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        EventBus.getDefault().postSticky(EventSample(EventSample.TYPE.MAIN_THEME_NIGHT))
        Toast.makeText(activity, "夜间主题还在开发中……\n可能没有那么美观……\n下次启动将会还原", Toast.LENGTH_LONG).show()
    }

    @Event(R.id.oivBlog)
    private fun onBtnClickBlog(@Suppress("UNUSED_PARAMETER") view: View) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sollyu.com")))
    }

    @Event(R.id.oivEMail)
    private fun onBtnClickEMail(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL  , "king.sollyu@gmail.com"        )
        intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name))
        startActivity(Intent.createChooser(intent, "Send Email"))
    }

    @Event(R.id.oivGithub)
    private fun onBtnClickGithub(@Suppress("UNUSED_PARAMETER") view: View) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kingsollyu/AppEnv")))
    }

    @Event(R.id.oivIssues)
    private fun onBtnClickIssues(@Suppress("UNUSED_PARAMETER") view: View) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kingsollyu/AppEnv-Kotlin/issues")))
    }

    @Event(R.id.oiwShowSystemApp)
    private fun onBtnClickShowSystemApp(@Suppress("UNUSED_PARAMETER") view: View) {
        Settings.Instance.isShowSystemApp = oiwShowSystemApp.isChecked
        EventBus.getDefault().postSticky(EventSample(EventSample.TYPE.MAIN_LIST_CLEAR))
        EventBus.getDefault().postSticky(EventSample(EventSample.TYPE.MAIN_REFRESH))
    }

    @Event(R.id.oiwShowDesktopIcon)
    private fun onBtnClickShowDesktopIcon(@Suppress("UNUSED_PARAMETER") view: View) {
        Settings.Instance.isShowDesktopIcon = oiwShowDesktopIcon.isChecked
        if (oiwShowDesktopIcon.isChecked) {
            packageManager.setComponentEnabledSetting(ComponentName(activity, "com.sollyu.android.appenv.activitys.ActivitySplashAlias"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
        }else{
            MaterialDialog.Builder(activity)
                    .title(R.string.settings_show_desktop_icon)
                    .content(R.string.settings_show_desktop_icon_tip)
                    .positiveText(android.R.string.ok)
                    .negativeText(android.R.string.cancel)
                    .onPositive { _, _ ->
                        Settings.Instance.isShowDesktopIcon = false
                        packageManager.setComponentEnabledSetting(ComponentName(activity, "com.sollyu.android.appenv.activitys.ActivitySplashAlias"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
                        oiwShowDesktopIcon.setCheckedImmediatelyNoEvent(Settings.Instance.isShowDesktopIcon)
                    }
                    .onNegative { _, _ ->
                        Settings.Instance.isShowDesktopIcon = true
                        packageManager.setComponentEnabledSetting(ComponentName(activity, "com.sollyu.android.appenv.activitys.ActivitySplashAlias"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
                        oiwShowDesktopIcon.setCheckedImmediatelyNoEvent(Settings.Instance.isShowDesktopIcon)
                    }
                    .show()
        }
    }

    @Event(R.id.oiwUseRoot)
    private fun onBtnClickUseRoot(view: View) {
        if (oiwUseRoot.isChecked) {
            MaterialDialog.Builder(activity)
                    .title(R.string.settings_use_root)
                    .content(R.string.settings_use_root_content)
                    .positiveText(android.R.string.ok)
                    .negativeText(android.R.string.cancel)
                    .onPositive { _, _ ->
                        if (Shell.SU.available()) {
                            Settings.Instance.isUseRoot = true
                        } else {
                            Settings.Instance.isUseRoot = false
                            Toast.makeText(activity, R.string.settings_use_root_available_fail, Toast.LENGTH_LONG).show()
                        }
                        oiwUseRoot.setCheckedImmediatelyNoEvent(Settings.Instance.isUseRoot)
                    }
                    .onNegative { _, _ ->
                        Settings.Instance.isUseRoot = false
                        oiwUseRoot.setCheckedImmediatelyNoEvent(Settings.Instance.isUseRoot)
                    }
                    .show()
        }else{
            Settings.Instance.isUseRoot = false
        }
    }

    @Event(R.id.oiwAppDataConfig)
    private fun onBtnClickUseSdConfig(view: View) {
        if (oiwAppDataConfig.isChecked) {
            MaterialDialog.Builder(activity)
                    .title(R.string.settings_use_app_data_config)
                    .content(R.string.settings_use_app_data_config_content)
                    .positiveText(android.R.string.ok)
                    .negativeText(android.R.string.cancel)
                    .onPositive { _, _ ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            MaterialDialog.Builder(activity).title(R.string.tip).content("7.0+系统不支持使用「内置存储」").positiveText(android.R.string.ok).show()
                            Settings.Instance.isUseAppDataConfig = false
                        }else{
                            Settings.Instance.isUseAppDataConfig = true
                        }
                        oiwAppDataConfig.setCheckedImmediatelyNoEvent(Settings.Instance.isUseAppDataConfig)
                        SettingsXposed.Save()
                        SettingsXposed.Reload()
                    }
                    .onNegative { _, _ ->
                        Settings.Instance.isUseAppDataConfig = false
                        oiwAppDataConfig.setCheckedImmediatelyNoEvent(Settings.Instance.isUseAppDataConfig)
                        SettingsXposed.Save()
                        SettingsXposed.Reload()
                    }
                    .show()
        }else{
            Settings.Instance.isUseAppDataConfig = false
            SettingsXposed.Save()
            SettingsXposed.Reload()
        }
    }

    @Event(R.id.oiwUseDataLocalTmp)
    private fun onBtnClickUseDataLocalTmp(view: View) {
        if (oiwUseDataLocalTmp.isChecked) {
            MaterialDialog.Builder(activity)
                    .title(R.string.settings_use_data_local_tmp_title)
                    .content(R.string.settings_use_data_local_tmp_content)
                    .positiveText(android.R.string.ok)
                    .negativeText(android.R.string.cancel)
                    .onPositive { dialog, which ->
                        if (Shell.SU.available()) {
                            try {
                                Settings.Instance.isUseDataLocalTmpConfig = true
                                SettingsXposed.Save()
                                SettingsXposed.Reload()
                            } catch (e: Exception) {
                                Settings.Instance.isUseDataLocalTmpConfig = false
                            }
                        }else {
                            Settings.Instance.isUseDataLocalTmpConfig = false
                        }
                        oiwAppDataConfig.switch.isEnabled = !Settings.Instance.isUseDataLocalTmpConfig
                        oiwUseDataLocalTmp.setCheckedImmediatelyNoEvent(Settings.Instance.isUseDataLocalTmpConfig)
                        SettingsXposed.Save()
                        SettingsXposed.Reload()
                    }
                    .onNegative { dialog, which ->
                        Settings.Instance.isUseDataLocalTmpConfig = false
                        oiwAppDataConfig.switch.isEnabled = !Settings.Instance.isUseDataLocalTmpConfig
                        oiwUseDataLocalTmp.setCheckedImmediatelyNoEvent(Settings.Instance.isUseDataLocalTmpConfig)
                        SettingsXposed.Save()
                        SettingsXposed.Reload()
                    }
                    .show()
        }else{
            Settings.Instance.isUseDataLocalTmpConfig = false
            oiwAppDataConfig.switch.isEnabled = !Settings.Instance.isUseDataLocalTmpConfig
            oiwUseDataLocalTmp.setCheckedImmediatelyNoEvent(Settings.Instance.isUseDataLocalTmpConfig)
            SettingsXposed.Save()
            SettingsXposed.Reload()
        }

    }

    @Event(R.id.oivLicence)
    private fun onBtnClickLicence(@Suppress("UNUSED_PARAMETER") view: View) {
        val notices = Notices()
        notices.addNotice(Notice("NotProguard"               , "https://github.com/kingsollyu/NotProguard"             , "Copyright 2017 Sollyu"                                                      , ApacheSoftwareLicense20()))
        notices.addNotice(Notice("OptionItem"                , "https://github.com/kingsollyu/OptionItem"              , "Copyright 2017 Sollyu"                                                      , ApacheSoftwareLicense20()))
        notices.addNotice(Notice("Apache Commons IO"         , "https://github.com/apache/commons-io"                  , "Apache License"                                                             , ApacheSoftwareLicense20()))
        notices.addNotice(Notice("BottomSheetBuilder"        , "https://github.com/rubensousa/BottomSheetBuilder"      , "Copyright 2016 Rúben Sousa"                                                 , ApacheSoftwareLicense20()))
        notices.addNotice(Notice("xUtils3"                   , "https://github.com/wyouflf/xUtils3"                    , "Copyright 2014-2015 wyouflf"                                                , ApacheSoftwareLicense20()))
        notices.addNotice(Notice("xLog"                      , "https://github.com/elvishew/xLog"                      , "Copyright 2016 Elvis Hew"                                                   , ApacheSoftwareLicense20()))
        notices.addNotice(Notice("fastjson"                  , "https://github.com/alibaba/fastjson"                   , "Copyright 1999-2016 Alibaba Group Holding Ltd."                             , ApacheSoftwareLicense20()))
        notices.addNotice(Notice("FloatingActionButton"      , "https://github.com/Clans/FloatingActionButton"         , "Copyright 2015 Dmytro Tarianyk"                                             , ApacheSoftwareLicense20()))
        notices.addNotice(Notice("EventBus"                  , "https://github.com/greenrobot/EventBus"                , "Copyright (C) 2012-2017 Markus Junginger greenrobot (http://greenrobot.org)", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("LicensesDialog"            , "https://github.com/PSDev/LicensesDialog"               , "Copyright 2013-2017 Philip Schiffer"                                        , ApacheSoftwareLicense20()))
        notices.addNotice(Notice("libsuperuser"              , "https://github.com/Chainfire/libsuperuser"             , "Written by and copyright ©: Jorrit \"Chainfire\" Jongma Author of SuperSU"  , ApacheSoftwareLicense20()))
        notices.addNotice(Notice("NoPermission"              , "https://github.com/NoNews/NoPermission"                , "Copyright 2017 Mike Antipiev and Alex Bykov"                                , ApacheSoftwareLicense20()))
        notices.addNotice(Notice("QrCodeScannerView-Android" , "https://github.com/qingmei2/QrCodeScannerView-Android" , "Copyright (c) 2017 qingmei2"                                                , ApacheSoftwareLicense20()))
        notices.addNotice(Notice("material-dialogs"          , "https://github.com/afollestad/material-dialogs"        , "Copyright (c) 2014-2016 Aidan Michael Follestad"                            , MITLicense())             )

        LicensesDialog.Builder(activity).setNotices(notices).build().showAppCompat()
    }

    @Event(R.id.oivUpdateSoftVersion)
    private fun onBtnClickUpdateSoftVersion(@Suppress("UNUSED_PARAMETER") view: View) {
        val materialDialog = MaterialDialog.Builder(activity).title(R.string.tip).content(R.string.settings_update_progress).progress(true, 0).cancelable(false).show()
        OkHttpClient().newCall(Request.Builder().url(activity.getString(R.string.online_url_soft)).build()).enqueue(object : Callback{
            override fun onFailure(request: Request, e: IOException) {
                materialDialog.dismiss()
                activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.error).content(e.message?:"null").show() }
            }

            override fun onResponse(response: Response) {
                materialDialog.dismiss()
                val contentJson = JSON.parseObject(response.body().string())
                activity.runOnUiThread {
                    if (contentJson.getIntValue("last-version-code") > BuildConfig.VERSION_CODE) {
                        MaterialDialog.Builder(activity)
                                .title(R.string.tip)
                                .content(R.string.settings_has_update, contentJson.getString("last-version-name"), contentJson.getString("last-version-message"))
                                .positiveText(R.string.settings_has_update_positive)
                                .negativeText(android.R.string.cancel)
                                .onPositive { _, _ -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(contentJson.getString("last-version-url")))) }
                                .show()
                    } else {
                        MaterialDialog.Builder(activity).title(R.string.tip).content(R.string.settings_no_update).positiveText(android.R.string.ok).show()
                    }
                }
            }
        })
    }



    @Event(R.id.oivThinks)
    private fun onBtnClickThinks(view: View) {
        MaterialDialog.Builder(activity)
                .title(R.string.settings_thinks)
                .items(arrayListOf("jfa2008@gmail.com"))
                .positiveText(android.R.string.ok)
                .show()
    }

    @Event(R.id.oivUpdatePhoneList)
    private fun onBtnClickUpdatePhoneList(@Suppress("UNUSED_PARAMETER") view: View) {
        val materialDialog = MaterialDialog.Builder(activity).title(R.string.tip).content(R.string.settings_update_progress).progress(true, 0).cancelable(false).show()
        OkHttpClient().newCall(Request.Builder().url(activity.getString(R.string.online_url_phone)).build()).enqueue(object :Callback{
            override fun onFailure(request: Request, e: IOException) {
                materialDialog.dismiss()
                activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.error).content(e.message?:"null").show() }
            }

            override fun onResponse(response: Response) {
                materialDialog.dismiss()
                val contentJson = JSON.parseObject(response.body().string())
                activity.runOnUiThread {
                    if (contentJson.getIntValue("VersionCode") > Phones.Instance.versionCode) {
                        MaterialDialog.Builder(activity)
                                .title(R.string.tip)
                                .content(R.string.settings_has_update, contentJson.getString("VersionName"), contentJson.getString("VersionCont"))
                                .positiveText(R.string.settings_has_update_positive)
                                .negativeText(android.R.string.cancel)
                                .onPositive { _, _ ->
                                    FileUtils.writeStringToFile(Phones.Instance.phoneFile, JSON.toJSONString(contentJson, true), "UTF-8")
                                    Phones.Reload()
                                    oivUpdatePhoneList.setRightText(Phones.Instance.versionName)
                                    Snackbar.make(oivLicence, R.string.settings_update_phone_success, Snackbar.LENGTH_LONG).show()
                                }
                                .show()
                    } else {
                        MaterialDialog.Builder(activity).title(R.string.tip).content(R.string.settings_no_update).positiveText(android.R.string.ok).show()
                    }
                }
            }
        })
    }

    @Event(R.id.oivLog)
    private fun onBtnClickLog(view: View) {
        ActivityLog.launch(activity)
    }
}
