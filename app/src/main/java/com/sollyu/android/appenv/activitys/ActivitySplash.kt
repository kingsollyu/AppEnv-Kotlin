/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.activitys

import android.Manifest
import android.app.ProgressDialog.show
import android.content.pm.PackageManager
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.elvishew.xlog.XLog
import com.sollyu.android.appenv.BuildConfig
import com.sollyu.android.appenv.R
import com.sollyu.android.appenv.commons.Application
import com.umeng.analytics.MobclickAgent
import ru.alexbykov.nopermission.PermissionHelper


/**
 * 作者：sollyu
 * 时间：2017/11/20
 * 说明：闪屏界面
 */
class ActivitySplash : ActivityBase(), Runnable {

    val permissionHelper by lazy { PermissionHelper(activity) }

    override fun run() {

        /* Xposed 没有成功的状态 */
        if (!Application.Instance.isXposedWork()) {
            MaterialDialog
                    .Builder(activity)
                    .title(R.string.splash_xposed_not_work_title)
                    .content(R.string.splash_xposed_not_work_content)
                    .positiveText(android.R.string.ok)
                    .onPositive { _, _ -> ActivityMain.launch(activity) }
                    .show()
            return
        }

        /* 状态检查结束、进入主界面 */
        ActivityMain.launch(activity)
    }

    override fun onInitDone() {
        super.onInitDone()

        permissionHelper.check(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissionHelper.onSuccess{
            Handler().postAtTime(this, 1000)
        }
        permissionHelper.onDenied {
            MaterialDialog.Builder(activity)
                    .title(R.string.tip)
                    .content(R.string.splash_permission_write_storage_denied_content)
                    .positiveText(android.R.string.ok).onPositive { _, _ -> Handler().postAtTime(this, 1000) }
                    .show()
        }
        permissionHelper.onNeverAskAgain {
            Toast.makeText(activity, R.string.splash_permission_write_storage_denied_content, Toast.LENGTH_LONG).show()
        }
        permissionHelper.run()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun getMobclickAgentTag(): String {
        return "Splash"
    }

}
