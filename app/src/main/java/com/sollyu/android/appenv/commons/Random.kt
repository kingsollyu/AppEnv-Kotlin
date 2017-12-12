/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.commons

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

    enum class SIM_O {
        CMCC/*中国移动*/, CUCC/*中国联通*/, CTCC/*中国电信*/
    }

    fun buildSerial(): String {
        return RandomStringGenerator.Builder().withinRange('0'.toInt(), 'Z'.toInt()).filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS).build().generate(RandomUtils.nextInt(6, 8))
    }

}