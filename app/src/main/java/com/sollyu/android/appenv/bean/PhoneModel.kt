package com.sollyu.android.appenv.bean

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

    var manufacturer: String? = null
    var model: String? = null
    var name: String? = null
}