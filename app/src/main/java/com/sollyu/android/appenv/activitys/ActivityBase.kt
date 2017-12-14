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
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.sollyu.android.appenv.commons.Settings
import com.sollyu.android.appenv.commons.SettingsXposed
import com.umeng.analytics.MobclickAgent

/**
 * 作者：sollyu
 * 时间：2017/11/20
 * 说明：基础的Activity类
 */
@SuppressLint("Registered")
abstract class ActivityBase : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onInitView()
        onInitData()
        onInitListener()
        Handler().post { onInitDone() }
    }

    open fun onInitView() {}

    open fun onInitData() {}

    open fun onInitListener() {}

    open fun onInitDone() {}

    abstract fun getMobclickAgentTag():String

    val activity: Activity
        get() = this

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart(getMobclickAgentTag())
        MobclickAgent.onResume(activity)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd(getMobclickAgentTag())
        MobclickAgent.onPause(activity)

        SettingsXposed.Instance.resetPermissions()
    }
}