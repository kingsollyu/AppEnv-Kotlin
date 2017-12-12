/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.activitys

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.text.method.LinkMovementMethod
import com.sollyu.android.appenv.BuildConfig
import com.sollyu.android.appenv.R
import com.sollyu.android.appenv.commons.Phones
import com.sollyu.android.appenv.commons.Settings
import com.sollyu.android.appenv.commons.SettingsXposed
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.content_activity_about.*

class ActivityAbout : ActivityBase() {

    companion object {
        fun launch(activity: Activity) {
            activity.startActivity(Intent(activity, ActivityAbout::class.java))
        }
    }

    override fun onInitView() {
        super.onInitView()
        setContentView(R.layout.activity_about)
        setSupportActionBar(toolbar)

        supportActionBar?.setTitle(R.string.title_activity_about)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onInitData() {
        super.onInitData()
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.isClickable = true
        textView.text = Html.fromHtml(getString(R.string.about_text,
                BuildConfig.VERSION_NAME,
                Settings.Instance.file.absolutePath,
                Phones.Instance.phoneFile.absolutePath,
                SettingsXposed.Instance.file.absolutePath))
    }

    override fun onInitListener() {
        super.onInitListener()
        fab.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kingsollyu/AppEnv-Kotlin/issues"))) }
    }
}
