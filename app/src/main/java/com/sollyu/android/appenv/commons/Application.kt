/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.commons

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatDelegate
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.XLog.logLevel
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.sollyu.android.appenv.BuildConfig
import com.sollyu.android.not.proguard.NotProguard
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.text.CharacterPredicates
import org.apache.commons.text.RandomStringGenerator

/**
 * 作者：sollyu
 * 时间：2017/11/20
 * 说明：
 */
class Application : android.app.Application(), Thread.UncaughtExceptionHandler {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var Instance: com.sollyu.android.appenv.commons.Application
            private set
    }

    init {
        Instance = this
    }

    override fun onCreate() {
        super.onCreate()

        // 初始化日志
        val logConfiguration = LogConfiguration.Builder().tag("Xposed").b().logLevel(if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.WARN).build()
        val logAndroid = AndroidPrinter()
        val logFile = FilePrinter.Builder(Instance.externalCacheDir.absolutePath).backupStrategy(NeverBackupStrategy()).fileNameGenerator(DateFileNameGenerator()).build()
        XLog.init(logConfiguration, logAndroid, logFile)
        XLog.d("[APP START]")

        // 添加崩溃捕获
        Thread.setDefaultUncaughtExceptionHandler(this)

        // 设置主题默认值
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // 友盟统计
        UMConfigure.init(Instance, "558a1cb667e58e7649000228", BuildConfig.FLAVOR, MobclickAgent.EScenarioType.E_UM_NORMAL.toValue(), "")

        // 初始化机型
        if (!Phones.Instance.phoneFile.exists()) {
            FileUtils.writeStringToFile(Phones.Instance.phoneFile, IOUtils.toString(Instance.assets.open("app.env.phone.json"), "UTF-8"), "UTF-8")
        }
    }

    override fun uncaughtException(t: Thread?, throwable: Throwable?) {
        XLog.e(throwable?.message, throwable)
    }

    @NotProguard
    open fun isXposedWork(): Boolean {
        return false
    }

}