/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.define;

/**
 * 作者：sollyu
 * 时间：2017/12/8
 * 说明：常量定义类
 */
public class AppEnvConstants {
    public static final String URL_APPENV_HOST             = "https://appenv.sollyu.com";
    public static final String URL_APPENV_SERVER           = URL_APPENV_HOST   + "/admin";
    public static final String URL_APPENV_UPLOAD_PACKAGE   = URL_APPENV_SERVER + "/api/upload/package";
    public static final String URL_APPENV_DOWNLOAD_PACKAGE = URL_APPENV_SERVER + "/api/download/package";
    public static final String URL_APPENV_SHARE_START      = URL_APPENV_HOST   + "/share";
    public static final String URL_APPENV_REPORT_PHONE     = URL_APPENV_HOST   + "/api/phone/report";
}
