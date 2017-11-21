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