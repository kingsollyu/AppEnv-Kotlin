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
import android.util.Log
import android.view.MenuItem
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.LinearLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSON
import com.elvishew.xlog.XLog
import com.just.agentweb.AgentWeb
import com.just.agentweb.AgentWebConfig
import com.just.agentweb.ChromeClientCallbackManager
import com.sollyu.android.appenv.R
import com.sollyu.android.appenv.commons.SettingsXposed
import com.sollyu.android.appenv.define.AppEnvConstants
import com.sollyu.android.appenv.events.EventSample
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import kotlinx.android.synthetic.main.activity_web.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.greenrobot.eventbus.EventBus
import java.io.IOException

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
        agentWeb.jsInterfaceHolder.addJavaObject("android", JsInterfaceHolder())
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

    inner class JsInterfaceHolder{

        @Suppress("unused")
        @JavascriptInterface
        fun register() {
            ActivityRegister.launch(activity)
        }

        @Suppress("unused")
        @JavascriptInterface
        fun downloadConfig(configId: String, packageName: String, configName: String, packageLabel: String) {
            MaterialDialog.Builder(activity)
                    .title(R.string.tip)
                    .content("确定下载：$configName 到 $packageLabel ?")
                    .positiveText("确定")
                    .negativeText(android.R.string.cancel)
                    .onPositive { dialog, _ ->
                        val cookie = AgentWebConfig.getCookiesByUrl(AppEnvConstants.URL_APPENV_SERVER)
                        OkHttpClient().newCall(Request.Builder().url(AppEnvConstants.URL_APPENV_DOWNLOAD_PACKAGE + "?config_id=" + configId).header("Cookie", cookie).build()).enqueue(object : Callback {
                            override fun onFailure(request: Request, e: IOException) {
                                dialog.dismiss()
                                XLog.e(e.toString(), e)
                                activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("下载出现错误：\n" + Log.getStackTraceString(e)).positiveText(android.R.string.ok).show() }
                            }

                            override fun onResponse(response: Response) {
                                dialog.dismiss()
                                val serverResult = response.body().string()
                                try {
                                    val resultJsonObject = JSON.parseObject(serverResult)
                                    if (resultJsonObject.getInteger("ret") == 200) {
                                        SettingsXposed.Instance.set(packageName, resultJsonObject.getJSONObject("data"))
                                        EventBus.getDefault().postSticky(EventSample(EventSample.TYPE.MAIN_REFRESH))
                                        activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("下载并应用成功").positiveText(android.R.string.ok).show() }
                                    } else {
                                        activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("下载出现错误：\n" + resultJsonObject.getString("msg")).positiveText(android.R.string.ok).show() }
                                    }
                                } catch (e: Exception) {
                                    activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("下载出现错误：\n请确定您已经正确的登陆").positiveText(android.R.string.ok).show() }
                                }
                            }
                        })
                    }
                    .show()
        }
    }

}
