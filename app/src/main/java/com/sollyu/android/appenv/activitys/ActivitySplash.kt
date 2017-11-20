package com.sollyu.android.appenv.activitys

import android.os.Handler
import com.afollestad.materialdialogs.MaterialDialog
import com.sollyu.android.appenv.BuildConfig
import com.sollyu.android.appenv.R
import com.sollyu.android.appenv.commons.Application


/**
 * 作者：sollyu
 * 时间：2017/11/20
 * 说明：闪屏界面
 */
class ActivitySplash : ActivityBase(), Runnable {
    override fun run() {

        /* Xposed 没有成功的状态 */
        if (!BuildConfig.DEBUG && !Application.Instance.isXposedWork()) {
            MaterialDialog
                    .Builder(activity)
                    .title(R.string.splash_xposed_not_work_title)
                    .content(R.string.splash_xposed_not_work_content)
                    .positiveText(android.R.string.ok)
                    .onPositive { _, _ -> activity.finish() }
                    .show()

            return
        }

        /* 状态检查结束、进入主界面 */
        ActivityMain.launch(activity)
    }

    override fun onInitDone() {
        super.onInitDone()

        Handler().postAtTime(this, 1000)
    }

}
