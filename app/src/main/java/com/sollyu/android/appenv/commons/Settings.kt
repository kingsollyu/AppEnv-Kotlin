package com.sollyu.android.appenv.commons

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.xiaoleilu.hutool.setting.Setting
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.Charset

/**
 * 作者：sollyu
 * 时间：2017/11/21
 * 说明：
 */
class Settings {
    companion object {
        val Instance = Settings()
    }

    private val defaultConfigFile: File by lazy { File(Application.Instance.getExternalFilesDir(null), "appenv.setting.json") }
    private var jsonObject: JSONObject = JSONObject()

    init {
        reload()
    }

    /**
     * 重新加载配置文件
     */
    @Synchronized
    fun reload() {
        var jsonObjectTmp = JSONObject()
        if (defaultConfigFile.exists()) {
            jsonObjectTmp = JSON.parseObject(FileUtils.readFileToString(defaultConfigFile, "UTF-8"))
        }
        jsonObject = jsonObjectTmp
    }

    /**
     * 保存配置文件
     */
    @Synchronized
    fun save() {
        FileUtils.write(defaultConfigFile, JSON.toJSONString(jsonObject, true), "UTF-8")
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
}