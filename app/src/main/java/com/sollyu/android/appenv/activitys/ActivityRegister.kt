/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

@file:Suppress("UNUSED_PARAMETER")

package com.sollyu.android.appenv.activitys

import android.app.Activity
import android.content.Intent
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSON
import com.sollyu.android.appenv.R
import com.sollyu.android.appenv.define.AppEnvConstants
import com.squareup.okhttp.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.xutils.view.annotation.Event
import org.xutils.x
import java.io.IOException

class ActivityRegister : ActivityBase() {

    companion object {
        fun launch(activity: Activity) {
            activity.startActivity(Intent(activity, ActivityRegister::class.java))
        }
    }

    override fun getMobclickAgentTag(): String {
        return "Register"
    }

    override fun onInitView() {
        super.onInitView()
        setContentView(R.layout.activity_register)
        x.view().inject(activity)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.register_title)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    @Suppress("unused")
    @Event(R.id.btnLogin)
    private fun onItemClickLogin(view: View) {

        val postBody = FormEncodingBuilder()
                .add("user_name"            , etUserName.text.toString())
                .add("user_password"        , etPwd.text.toString())
                .add("user_password_confirm", etPwdConfirm.text.toString())
                .add("user_email"           , etEMail.text.toString())
                .build()

        val materialDialog = MaterialDialog.Builder(activity).title(R.string.tip).content(R.string.register_processing).progress(true, 0).cancelable(false).show()
        OkHttpClient().newCall(Request.Builder().url(AppEnvConstants.URL_APPENV_REGISTER).post(postBody).build()).enqueue(object : Callback{
            override fun onFailure(request: Request, e: IOException) {
                materialDialog.dismiss()
                activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.error).content(e.message?:"null").show() }
            }

            override fun onResponse(response: Response) {
                materialDialog.dismiss()
                val serverResult = response.body().string()
                try {
                    val resultJsonObject = JSON.parseObject(serverResult)
                    if (resultJsonObject.getInteger("ret") == 200) {
                        activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("注册成功").canceledOnTouchOutside(false).positiveText(android.R.string.ok).onPositive { _, _ -> activity.finish() }.show() }
                    } else {
                        activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("注册失败：\n" + resultJsonObject.getString("msg")).positiveText(android.R.string.ok).show() }
                    }
                } catch (e: Exception) {
                    activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("注册失败，原因未知").positiveText(android.R.string.ok).show() }
                }
            }
        })
    }
}
