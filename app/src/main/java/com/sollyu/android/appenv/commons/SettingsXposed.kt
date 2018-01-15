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
import eu.chainfire.libsuperuser.Shell


/**
 * 作者：sollyu
 * 时间：2017/11/21
 * 说明：
 */
class SettingsXposed {
    companion object {
        var Instance = SettingsXposed()

        @Synchronized
        fun Reload() {
            Instance = SettingsXposed()
        }

        @Synchronized
        fun Save() {
            when {
                Settings.Instance.isUseDataLocalTmpConfig -> {
                    XLog.d("use data local tmp")
                    // 使用Root命令进行创建
                    if (!Instance.fileDataLocalConfig.exists()) {
                        Shell.SU.run("touch /data/local/tmp/appenv.xposed.json")
                        Shell.SU.run("chmod 777 /data/local/tmp/appenv.xposed.json")
                    }

                    // 文件依然还不存在
                    if (!Instance.fileDataLocalConfig.exists()) {
                        XLog.e("/data/local/tmp/appenv.xposed.json not exist")
                        throw RuntimeException("/data/local/tmp/appenv.xposed.json not exist")
                    }

                    // 文件没有写的权限
                    if (!Instance.fileDataLocalConfig.canWrite()) {
                        Shell.SU.run("chmod 777 /data/local/tmp/appenv.xposed.json")
                    }
                    // 文件依然没有写的权限
                    if (!Instance.fileDataLocalConfig.canWrite()) {
                        XLog.e("/data/local/tmp/appenv.xposed.json can not write")
                        throw RuntimeException("/data/local/tmp/appenv.xposed.json can not write")
                    }

                    FileUtils.write(Instance.fileDataLocalConfig, JSON.toJSONString(Instance.jsonObject, true), "UTF-8")
                    try { FileUtils.forceDelete(Instance.fileAppDataConfig) } catch (e: Exception) { }
                    try { FileUtils.forceDelete(Instance.fileExtendConfig)  } catch (e: Exception) { }
                }
                Settings.Instance.isUseAppDataConfig -> {
                    XLog.d("use app data")
                    FileUtils.write(Instance.fileAppDataConfig, JSON.toJSONString(Instance.jsonObject, true), "UTF-8")
                    try { FileUtils.forceDelete(Instance.fileExtendConfig) } catch (e: Exception) { }
                    try { Shell.SU.run("rm " + Instance.fileDataLocalConfig) } catch (e: Exception) { }
                }
                else -> {
                    XLog.d("use ext")
                    FileUtils.write(Instance.fileExtendConfig, JSON.toJSONString(Instance.jsonObject, true), "UTF-8")
                    try { FileUtils.forceDelete(Instance.fileAppDataConfig) } catch (e: Exception) { }
                    try { Shell.SU.run("rm " + Instance.fileDataLocalConfig) } catch (e: Exception) { XLog.e(e.message, e) }
                }
            }
        }
    }

    private val fileAppDataConfig   = File(Application.Instance.filesDir, "appenv.xposed.json")
    private val fileExtendConfig    = File(Application.Instance.getExternalFilesDir(null), "appenv.xposed.json")
    private val fileDataLocalConfig = File("/data/local/tmp/appenv.xposed.json")

    val file: File by lazy {
        if (Settings.Instance.isUseAppDataConfig)
            fileAppDataConfig
        else if (Settings.Instance.isUseDataLocalTmpConfig)
            fileDataLocalConfig
        else
            fileExtendConfig
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