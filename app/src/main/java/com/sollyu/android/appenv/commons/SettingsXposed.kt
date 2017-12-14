/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.commons

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import org.apache.commons.io.FileUtils
import java.io.File
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
        if (Settings.Instance.isUseAppDataConfig)
            File(Application.Instance.filesDir, "appenv.xposed.json")
        else
            File(Application.Instance.getExternalFilesDir(null), "appenv.xposed.json")
    }

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
        if (file.exists()) {
            jsonObjectTmp = JSON.parseObject(FileUtils.readFileToString(file, "UTF-8"))
        }
        jsonObject = jsonObjectTmp
    }

    /**
     * 保存配置文件
     */
    @Synchronized
    fun save() {
        FileUtils.write(file, JSON.toJSONString(jsonObject, true), "UTF-8")
    }

    fun resetPermissions() {
        if (Settings.Instance.isUseAppDataConfig) {
            XLog.d("${file.absolutePath} resetPermissions")
            file.setReadable(true, false)
            file.setWritable(true, false)
            file.setExecutable(true,false)
        }
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