/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.commons.libs

/**
 * 作者：sollyu
 * 时间：2017/12/12
 * 说明：
 */
object IMEIGen {
    /**
     * IMEI 校验码

     * @param code
     * *
     * @return
     */
    fun genCode(code: String): String {
        var sum1 = 0
        var sum2 = 0
        var temp: Int
        val chs = code.toCharArray()
        for (i in chs.indices) {
            val num = chs[i] - '0'
            if (i % 2 == 0) {
                sum1 += num
            } else {
                temp = num * 2
                if (temp < 10) {
                    sum2 += temp
                } else {
                    sum2 = sum2 + temp + 1 - 10
                }
            }
        }
        val total = sum1 + sum2
        /*如果得出的数个位是0则校验位为0,否则为10减去个位数 */
        return if (total % 10 == 0) {
            "0"
        } else {
            (10 - total % 10).toString() + ""
        }
    }

}