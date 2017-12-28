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
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import com.elvishew.xlog.XLog
import com.sollyu.android.appenv.R
import com.sollyu.android.appenv.commons.Application
import kotlinx.android.synthetic.main.activity_log.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.apache.commons.io.FileUtils
import java.io.File

class ActivityLog : ActivityBase() {
    companion object {
        fun launch(activity: Activity) {
            activity.startActivity(Intent(activity, ActivityLog::class.java))
        }
    }

    override fun getMobclickAgentTag(): String {
        return "Log"
    }

    override fun onInitView() {
        super.onInitView()
        setContentView(R.layout.activity_log)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.settings_log)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        textView.movementMethod = ScrollingMovementMethod.getInstance()
        textView.setHorizontallyScrolling(true)
    }

    override fun onInitData() {
        super.onInitData()
        onItemClickReloadLog()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_log, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuDeleteLog -> {
                this.onItemClickDeleteLog()
            }
            R.id.menuReloadLog -> {
                this.onItemClickReloadLog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onItemClickDeleteLog() {
        val logFile = File(Application.Instance.externalCacheDir.absolutePath, "log")
        if (logFile.exists()) {
            FileUtils.forceDelete(logFile)
        }
        onItemClickReloadLog()
    }

    private fun onItemClickReloadLog() {
        val logFile = File(Application.Instance.externalCacheDir.absolutePath, "log")
        if (logFile.exists() && logFile.canRead()) {
            textView.text = FileUtils.readFileToString(logFile, "UTF-8")
        } else {
            textView.text = ""
        }
    }
}
