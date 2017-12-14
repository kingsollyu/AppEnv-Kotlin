/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.commons

import android.content.Context
import android.content.SharedPreferences
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.sollyu.android.appenv.BuildConfig
import org.apache.commons.io.FileUtils
import java.io.File
import android.content.Context.MODE_PRIVATE
import android.os.Environment
import com.elvishew.xlog.XLog


/**
 * 作者：sollyu
 * 时间：2017/11/21
 * 说明：
 */
class SettingsXposed {
    companion object {
        val Instance = SettingsXposed()
    }

    val file: File by lazy {
        if (Settings.Instance.isSdConfig)
            File(Application.Instance.getExternalFilesDir(null), "appenv.xposed.json")
        else
            File(Environment.getDataDirectory(),"data/" + BuildConfig.APPLICATION_ID + "/shared_prefs/appenv.xposed.xml")
    }

    val sharedPreferences: SharedPreferences by lazy { Application.Instance.getSharedPreferences("appenv.xposed", Context.MODE_PRIVATE) }
    var jsonObject = JSONObject()

    init {
        reload()
    }

    /**
     * 重新加载配置文件
     */
    @Synchronized
    fun reload() {
        var jsonObjectTmp = JSONObject()
        if (Settings.Instance.isSdConfig) {
            if (file.exists()) {
                jsonObjectTmp = JSON.parseObject(FileUtils.readFileToString(file, "UTF-8"))
            }
        } else {
            jsonObjectTmp = JSON.parseObject(sharedPreferences.getString("xposedConfig", "{}"))
        }
        jsonObject = jsonObjectTmp

    }

    /**
     * 保存配置文件
     */
    @Synchronized
    fun save() {
        sharedPreferences.edit().putString("configFile", file.absolutePath).apply()

        if (Settings.Instance.isSdConfig) {
            FileUtils.write(file, JSON.toJSONString(jsonObject, true), "UTF-8")
        }else{
            sharedPreferences.edit().putString("xposedConfig", jsonObject.toJSONString()).apply()
        }
    }

    fun resetPermissions() {
        XLog.d("resetPermissions")
        file.setReadable(true, false)
        file.setWritable(true, false)
    }

    /**
     * 删除一个配置文件
     */
    fun remove(packageName: String) {
        jsonObject.remove(packageName)
        save()
    }

    fun get(packageName: String): JSONObject? {
        return jsonObject.getJSONObject(packageName)
    }

    fun set(packageName: String, jsonObject: JSONObject) {
        this.jsonObject.put(packageName, jsonObject)
        this.save()
    }
}