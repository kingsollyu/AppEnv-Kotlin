package com.sollyu.android.appenv.activitys

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.sollyu.android.appenv.R
import de.psdev.licensesdialog.LicensesDialog
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20
import de.psdev.licensesdialog.licenses.MITLicense
import de.psdev.licensesdialog.model.Notice
import de.psdev.licensesdialog.model.Notices
import kotlinx.android.synthetic.main.include_toolbar.*
import org.xutils.view.annotation.Event
import org.xutils.x

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

    @Event(R.id.oivBlog)
    private fun onBtnClickBlog(view: View) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sollyu.com")))
    }

    @Event(R.id.oivEMail)
    private fun onBtnClickEMail(view: View) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mailto:king.sollyu@gmail.com")))
    }

    @Event(R.id.oivGithub)
    private fun onBtnClickGithub(view: View) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kingsollyu/AppEnv")))
    }

    @Event(R.id.oivLicence)
    private fun onBtnClickLicence(view: View) {
        val notices = Notices()
        notices.addNotice(Notice("NotProguard", "https://github.com/kingsollyu/NotProguard", "Copyright 2017 Sollyu", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("OptionItem", "https://github.com/kingsollyu/OptionItem", "Copyright 2017 Sollyu", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("LibSuperUser", "https://github.com/kingsollyu/LibSuperUser", "Copyright 2017 Sollyu", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("Apache Commons IO", "https://github.com/apache/commons-io", "Apache License", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("xUtils3", "https://github.com/wyouflf/xUtils3", "Copyright 2014-2015 wyouflf", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("xLog", "https://github.com/elvishew/xLog", "Copyright 2016 Elvis Hew", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("fastjson", "https://github.com/alibaba/fastjson", "Copyright 1999-2016 Alibaba Group Holding Ltd.", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("FloatingActionButton", "https://github.com/Clans/FloatingActionButton", "Copyright 2015 Dmytro Tarianyk", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("BottomSheetBuilder", "https://github.com/rubensousa/BottomSheetBuilder", "Copyright 2016 Rúben Sousa", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("EventBus", "https://github.com/greenrobot/EventBus", "Copyright (C) 2012-2017 Markus Junginger, greenrobot (http://greenrobot.org)", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("hutool", "https://github.com/looly/hutool", "Apache License", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("LicensesDialog", "https://github.com/PSDev/LicensesDialog", "Copyright 2013-2017 Philip Schiffer", ApacheSoftwareLicense20()))
        notices.addNotice(Notice("material-dialogs", "https://github.com/afollestad/material-dialogs", "Copyright (c) 2014-2016 Aidan Michael Follestad", MITLicense()))

        LicensesDialog.Builder(activity).setNotices(notices).build().showAppCompat()
    }

}
