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
 * 时间：2017/12/12
 * 说明：
 */
class Solution {
    companion object {
        val Instance = Solution()
    }

    private val defaultConfigFile: File by lazy { File(Application.Instance.getExternalFilesDir(null), "appenv.solution.json") }

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