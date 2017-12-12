/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.bean

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.annotation.JSONField

/**
 * 作者：sollyu
 * 时间：2017/12/8
 * 说明：
 */
class PhoneModel {

    constructor()

    constructor(manufacturer: String?, model: String?, name: String?) {
        this.manufacturer = manufacturer
        this.model = model
        this.name = name
    }

    @JSONField(name = "buildManufacturer")
    var manufacturer: String? = null

    @JSONField(name = "buildModel")
    var model: String? = null

    @JSONField(name = "phoneName")
    var name: String? = null

    @JSONField(serialize = false)
    override fun toString(): String {
        return JSON.toJSONString(this)
    }
}