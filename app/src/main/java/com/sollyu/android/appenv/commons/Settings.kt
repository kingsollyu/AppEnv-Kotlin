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

/**
 * 作者：sollyu
 * 时间：2017/11/21
 * 说明：
 */
class Settings {
    companion object {
        val Instance = Settings()
    }

    val file: File by lazy { File(Application.Instance.getExternalFilesDir(null), "appenv.setting.json") }
    var jsonObject: JSONObject = JSONObject()

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

    /**
     * 删除一个配置文件
     */
    fun remove(keyName: String) {
        jsonObject.remove(keyName)
        save()
    }

    /**
     * 是否显示系统程序
     */
    var isShowSystemApp: Boolean
        get() = jsonObject.getBooleanValue("isShowSystemApp")
        set(value) = jsonObject.put("isShowSystemApp", value).let { save() }

    /**
     * 是否显示桌面图标
     */
    var isShowDesktopIcon: Boolean
        get() = jsonObject.getBoolean("isShowDesktopIcon") ?: true
        set(value) = jsonObject.put("isShowDesktopIcon", value).let { save() }

    /**
     * 是否直接使用root权限
     */
    var isUseRoot: Boolean
        get() = jsonObject.getBoolean("isUseRoot") ?: false
        set(value) = jsonObject.put("isUseRoot", value).let { save() }

    /**
     * 是否使用SD卡配置
     */
    var isUseAppDataConfig: Boolean
        get() = jsonObject.getBoolean("isUseAppDataConfig") ?: false
        set(value) = jsonObject.put("isUseAppDataConfig", value).let { save() }
}