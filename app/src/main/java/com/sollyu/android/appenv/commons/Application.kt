package com.sollyu.android.appenv.commons

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatDelegate
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.sollyu.android.appenv.BuildConfig
import com.sollyu.android.not.proguard.NotProguard

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


    override fun onCreate() {
        super.onCreate()

        Instance = this

        // 初始化日志
        val logConfiguration = LogConfiguration.Builder().tag("Xposed").logLevel(if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.WARN).build()
        val logAndroid = AndroidPrinter()
        val logFile = FilePrinter.Builder(Instance.externalCacheDir.absolutePath).backupStrategy(NeverBackupStrategy()).fileNameGenerator(DateFileNameGenerator()).build()
        XLog.init(logConfiguration, logAndroid, logFile)
        XLog.d("[APP START]")

        // 添加崩溃捕获
        Thread.setDefaultUncaughtExceptionHandler(this)

        // 设置主题默认值
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    override fun uncaughtException(t: Thread?, throwable: Throwable?) {
        XLog.e(throwable?.message, throwable)
    }

    @NotProguard
    open fun isXposedWork(): Boolean {
        return false
    }

}