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
import android.view.MenuItem
import android.webkit.WebView
import android.widget.LinearLayout
import com.just.agentweb.AgentWeb
import com.just.agentweb.ChromeClientCallbackManager
import com.sollyu.android.appenv.R
import kotlinx.android.synthetic.main.activity_web.*
import kotlinx.android.synthetic.main.include_toolbar.*

class ActivityWeb : ActivityBase(), ChromeClientCallbackManager.ReceivedTitleCallback {
    companion object {
        fun launch(activity: Activity, webTitle: String, webUrl: String) {
            val intent = Intent(activity, ActivityWeb::class.java)

            intent.putExtra("webTitle", webTitle)
            intent.putExtra("webUrl", webUrl)
            activity.startActivity(intent)
        }
    }

    val agentWeb by lazy {
        AgentWeb.with(activity)
                .setAgentWebParent(linearLayout, LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .defaultProgressBarColor()
                .setReceivedTitleCallback(this)
                .createAgentWeb()
                .ready()
                .go(intent.getStringExtra("webUrl"))
    }

    override fun getMobclickAgentTag(): String {
        return "Web"
    }

    override fun onInitView() {
        super.onInitView()
        setContentView(R.layout.activity_web)

        setSupportActionBar(toolbar)
        supportActionBar?.title = intent.getStringExtra("webTitle")
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onInitDone() {
        super.onInitDone()
        agentWeb.agentWebSettings
    }

    override fun onReceivedTitle(view: WebView, title: String) {
        supportActionBar?.title = title
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (agentWeb.back())
            return true

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        agentWeb.webLifeCycle.onPause()
    }

    override fun onResume() {
        super.onResume()
        agentWeb.webLifeCycle.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        agentWeb.webLifeCycle.onDestroy()
    }
}
