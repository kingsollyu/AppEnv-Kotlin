package com.sollyu.android.appenv.activitys

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

/**
 * 作者：sollyu
 * 时间：2017/11/20
 * 说明：基础的Activity类
 */
@SuppressLint("Registered")
open class ActivityBase : AppCompatActivity() {

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

    val activity: Activity
        get() = this

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}