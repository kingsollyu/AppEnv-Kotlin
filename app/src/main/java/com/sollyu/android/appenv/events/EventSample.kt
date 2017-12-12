/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.events

/**
 * 作者：sollyu
 * 时间：2017/10/30
 * 说明：
 */
class EventSample(val eventTYPE: TYPE) {
    enum class TYPE {
        MAIN_REFRESH, MAIN_LIST_CLEAR
    }
}