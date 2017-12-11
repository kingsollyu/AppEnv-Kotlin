package com.sollyu.android.appenv.bean

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
}