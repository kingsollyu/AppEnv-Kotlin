/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.activitys

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.fastjson.JSON
import com.elvishew.xlog.XLog
import com.sollyu.android.appenv.R
import com.sollyu.android.appenv.commons.SettingsXposed
import com.sollyu.android.appenv.define.AppEnvConstants
import com.sollyu.android.appenv.events.EventSample
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import kotlinx.android.synthetic.main.activity_scan_qr.*
import kotlinx.android.synthetic.main.activity_scan_qr.view.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.greenrobot.eventbus.EventBus
import ru.alexbykov.nopermission.PermissionHelper
import java.io.IOException

class ActivityScanQR : ActivityBase(){
    private val permissionHelper by lazy { PermissionHelper(activity) }
    private val fromActivity by lazy { activity.intent.getIntExtra("fromActivity", ActivityScanQR.FROM_DETAIL) }

    companion object {
        val FROM_DETAIL = 1

        fun launch(activity: Activity, fromActivity: Int) {
            val intent = Intent(activity, ActivityScanQR::class.java)
            intent.putExtra("fromActivity", fromActivity)
            activity.startActivity(intent)
        }
    }

    override fun getMobclickAgentTag(): String {
        return "ScanQR"
    }

    override fun onInitView() {
        super.onInitView()
        setContentView(R.layout.activity_scan_qr)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.scan_qr)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onInitDone() {
        super.onInitDone()

        permissionHelper.check(Manifest.permission.CAMERA)
        permissionHelper.onSuccess{
            var justOnce = 0
            qrCodeReaderView.setQRDecodingEnabled(true);
            qrCodeReaderView.setAutofocusInterval(2000L)
            qrCodeReaderView.setTorchEnabled(true)
            qrCodeReaderView.setOnQRCodeReadListener { text, _ ->
                qrCodeReaderView.stopCamera()
                if (text.startsWith(AppEnvConstants.URL_APPENV_SHARE_START)) {

                    if (justOnce > 0) {
                        return@setOnQRCodeReadListener
                    }
                    justOnce += 1

                    val materialDialog = MaterialDialog.Builder(activity).title(R.string.tip).content("下载中……").progress(true, 0).cancelable(false).show()
                    OkHttpClient().newCall(Request.Builder().url(text).build()).enqueue(object : Callback {
                        override fun onFailure(request: Request, e: IOException) {
                            materialDialog.dismiss()
                            activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("下载出现错误：\n" + Log.getStackTraceString(e)).positiveText(android.R.string.ok).show() }
                        }

                        override fun onResponse(response: Response) {
                            materialDialog.dismiss()
                            val serverResult = response.body().string()
                            try {
                                val resultJsonObject = JSON.parseObject(serverResult)
                                if (resultJsonObject.getInteger("ret") == 200) {
                                    if (fromActivity == FROM_DETAIL) {
                                        EventBus.getDefault().post(EventSample(EventSample.TYPE.DETAIL_JSON_2_UI, resultJsonObject.getJSONObject("data")))
                                        activity.finish()
                                    }
                                } else {
                                    qrCodeReaderView.startCamera()
                                    activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("下载出现错误：\n" + resultJsonObject.getString("msg")).positiveText(android.R.string.ok).show() }
                                }
                            } catch (e: Exception) {
                                qrCodeReaderView.startCamera()
                                activity.runOnUiThread { MaterialDialog.Builder(activity).title(R.string.tip).content("下载出现错误").positiveText(android.R.string.ok).show() }
                            }
                        }
                    })
                } else {
                    MaterialDialog.Builder(activity).title(R.string.tip).content("此二维码不是应用变量分享的内容").onAny { _, _ -> qrCodeReaderView.startCamera() }.show()
                }
            }
            qrCodeReaderView.setBackCamera()
            qrCodeReaderView.startCamera()
        }
        permissionHelper.onDenied {
            MaterialDialog.Builder(activity)
                    .title(R.string.tip)
                    .content(R.string.splash_permission_write_storage_denied_content)
                    .positiveText(android.R.string.ok)
                    .show()
        }
        permissionHelper.onNeverAskAgain {
            Toast.makeText(activity, R.string.splash_permission_write_storage_denied_content, Toast.LENGTH_LONG).show()
        }
        permissionHelper.run()
    }

    override fun onResume() {
        super.onResume()
        qrCodeReaderView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        qrCodeReaderView.stopCamera()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
