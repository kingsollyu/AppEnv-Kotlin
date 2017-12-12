/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.commons

import android.os.Build
import com.sollyu.android.appenv.R.string.random
import com.sollyu.android.appenv.commons.libs.IMEIGen
import com.sollyu.android.appenv.commons.libs.RandomMac
import org.apache.commons.lang3.RandomUtils
import org.apache.commons.text.CharacterPredicates
import org.apache.commons.text.RandomStringGenerator

/**
 * 作者：sollyu
 * 时间：2017/12/12
 * 说明：
 */
class Random {

    companion object {
        /**
         * 创建一个新的随机对象
         */
        fun New(): Random {
            return Random()
        }

        /**
         * @param min 最小
         * @param max 最大
         * @return 随机数
         * @author sollyu
         */
        fun Int(min: Int, max:Int): Int {
            return java.util.Random().nextInt(max + 1 - min) + min
        }

    }

    enum class ANDROID_VERSION(val versionName: String, val versionCode: Int) {
        ICE_CREAM_SANDWICH_MR1("4.0.3", Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)  ,
        JELLY_BEAN_MR1        ("4.2"  , Build.VERSION_CODES.JELLY_BEAN_MR1)          ,
        KITKAT                ("4.4"  , Build.VERSION_CODES.KITKAT)                  ,
        KITKAT_WATCH          ("4.4W" , Build.VERSION_CODES.KITKAT_WATCH)            ,
        LOLLIPOP_MR1          ("5.0"  , Build.VERSION_CODES.LOLLIPOP_MR1)            ,
        M                     ("6.0"  , Build.VERSION_CODES.M)                       ,
        N                     ("7.0"  , Build.VERSION_CODES.N)                       ,
        ;

        companion object {
            fun get(versionName: String): ANDROID_VERSION = ANDROID_VERSION.values().first { it.versionName == versionName }
        }
    }

    /**
     *
     */
    enum class SIM_TYPE(val label: String, val simCode: String, val simIccid: String) {
        CMCC("中国移动", "46000", "898600"),
        CUCC("中国联通", "46001", "898601"),
        CTCC("中国电信", "46003", "898603");

        companion object {
            fun get(label: String): SIM_TYPE = SIM_TYPE.values().first { it.label == label }
        }
    }

    private val simType = SIM_TYPE.values()[RandomUtils.nextInt(0, SIM_TYPE.values().size - 1)]

    init {

    }

    /**
     *
     */
    fun buildSerial(): String {
        return RandomStringGenerator.Builder().withinRange('0'.toInt(), 'Z'.toInt()).filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS).build().generate(RandomUtils.nextInt(6, 8))
    }

    /**
     *
     */
    fun androidId(): String {
        return RandomStringGenerator.Builder().withinRange('0'.toInt(), 'z'.toInt()).filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS).build().generate(RandomUtils.nextInt(10, 13))
    }

    /**
     *
     */
    fun simLine1Number(): String {
        val telFirst = arrayOf("134", "135", "136", "137", "138", "139", "150", "151", "152", "157", "158", "159", "130", "131", "132", "155", "156", "133", "153")
        var line1Number = ""

        val isUserArea = RandomUtils.nextInt(0, 100) < 30
        if (isUserArea) line1Number += "+86"

        return line1Number + telFirst[RandomUtils.nextInt(0, telFirst.size - 1)] + RandomStringGenerator.Builder().withinRange('0'.toInt(), '9'.toInt()).filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS).build().generate(8)
    }

    /**
     *
     */
    fun simGetDeviceId(): String {
        val simDeviceId = "86" + RandomStringGenerator.Builder().withinRange('0'.toInt(), '9'.toInt()).build().generate(12)
        return simDeviceId + IMEIGen.genCode(simDeviceId)
    }

    /**
     *
     */
    fun simSubscriberId(simType: SIM_TYPE): String {
        return simType.simCode + RandomStringGenerator.Builder().withinRange('0'.toInt(), '9'.toInt()).build().generate(10)
    }

    /**
     *
     */
    fun simOperator():String {
        return "46000"
    }

    fun simSerialNumber(simType: SIM_TYPE): String {
        return simType.simIccid + RandomStringGenerator.Builder().withinRange('0'.toInt(), '9'.toInt()).build().generate(14)
    }

    fun wifiName(): String {
        val strings = arrayOf("TP-", "FAST_", "Tenda_", "TP-LINK_", "MERCURY_")
        return strings[RandomUtils.nextInt(0, strings.size-1)] + RandomStringGenerator.Builder().withinRange('0'.toInt(), 'Z'.toInt()).filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS).build().generate(RandomUtils.nextInt(6, 8))
    }

    fun wifiMacAddress(): String {
        return RandomMac.getMacAddrWithFormat(":")
    }

}