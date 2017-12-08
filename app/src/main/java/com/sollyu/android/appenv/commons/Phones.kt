package com.sollyu.android.appenv.commons

import com.sollyu.android.appenv.bean.PhoneModel
import java.util.*

/**
 * 作者：sollyu
 * 时间：2017/12/8
 * 说明：
 */
class Phones {
    companion object {
        var Instance = Phones()
    }

    var versionName = "1.0.1"
    var versionCode = 1
    val phoneManufacturer = LinkedHashMap<String, ArrayList<PhoneModel>>()


}