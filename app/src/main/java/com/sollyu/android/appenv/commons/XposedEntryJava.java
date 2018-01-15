/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.commons;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.sollyu.android.appenv.BuildConfig;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 作者：sollyu
 * 时间：2018/1/2
 * 说明：
 */
public class XposedEntryJava implements IXposedHookLoadPackage {
    private static final String TAG = "XposedEntryJava";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        ArrayList<String> ignoreApplicationList = new ArrayList<>();
        ignoreApplicationList.add("android");
        ignoreApplicationList.add("de.robv.android.xposed.installer");
        Log.d(TAG, "handleLoadPackage: " + loadPackageParam.packageName);
        if (ignoreApplicationList.contains(loadPackageParam.packageName)) {
            return;
        }

        if (loadPackageParam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            XposedBridge.hookAllMethods(XposedHelpers.findClass("com.sollyu.android.appenv.commons.Application", loadPackageParam.classLoader), "isXposedWork", new MethodHookValue(true));
            return;
        }

        // 加载文件
        File xposedSettingsFile = null;
        do {
            // 检查/data/local/tmp/appenv.xposed.json
            xposedSettingsFile = new File("/data/local/tmp/appenv.xposed.json");
            if (xposedSettingsFile.exists() && xposedSettingsFile.canRead())
                break;

            // 检查内置存储
            xposedSettingsFile = new File(Environment.getDataDirectory(), "data/" + BuildConfig.APPLICATION_ID + "/files/appenv.xposed.json");
            if (xposedSettingsFile.exists() && xposedSettingsFile.canRead())
                break;

            // 检查sd卡存储
            xposedSettingsFile = new File("/sdcard/Android/data/" + BuildConfig.APPLICATION_ID + "/files/appenv.xposed.json");
            if (xposedSettingsFile.exists() && xposedSettingsFile.canRead())
                break;

            xposedSettingsFile = null;
        }while (false);

        if (xposedSettingsFile == null) {
            Log.e(TAG, "handleLoadPackage: xposedSettingsFile is null");
            return;
        }

        /* 如果配置内容为空 */
        String xposedSettingsFileContent = null;
        try {
            xposedSettingsFileContent = FileUtils.readFileToString(xposedSettingsFile, "UTF-8");
        } catch (Throwable throwable) {
            Log.e(TAG, "handleLoadPackage: " + throwable.getMessage(), throwable);
            return;
        }
        if (xposedSettingsFileContent == null || xposedSettingsFileContent.isEmpty()) {
            Log.w(TAG, "handleLoadPackage: xposedSettingsFileContent == null");
            return;
        }

        /* 将内容转化成json对象 */
        JSONObject xposedSettingsJson = null;
        try {
            xposedSettingsJson = new JSONObject(xposedSettingsFileContent);
        } catch (Throwable throwable) {
            Log.e(TAG, "handleLoadPackage: " + throwable.getMessage(), throwable);
            return;
        }

        /* 拦截制定包名 */
        if (xposedSettingsJson.has(loadPackageParam.packageName)) {
            final JSONObject              xposedPackageJson = xposedSettingsJson.getJSONObject(loadPackageParam.packageName);
            final HashMap<String, Object> buildValueHashMap = new HashMap<String, Object>();

            Log.d(TAG, "handleLoadPackage: " + xposedPackageJson.toString());

            if (xposedPackageJson.has("android.os.Build.ro.product.manufacturer")) {
                String jsonValue = xposedPackageJson.getString("android.os.Build.ro.product.manufacturer");
                XposedHelpers.setStaticObjectField(Build.class, "MANUFACTURER", jsonValue);
                XposedHelpers.setStaticObjectField(Build.class, "PRODUCT"     , jsonValue);
                XposedHelpers.setStaticObjectField(Build.class, "BRAND"       , jsonValue);
                buildValueHashMap.put("ro.product.manufacturer", jsonValue);
                buildValueHashMap.put("ro.product.brand"       , jsonValue);
                buildValueHashMap.put("ro.product.name"        , jsonValue);
            }
            if (xposedPackageJson.has("android.os.Build.ro.product.model")) {
                String jsonValue = xposedPackageJson.getString("android.os.Build.ro.product.model");
                XposedHelpers.setStaticObjectField(Build.class, "MODEL" , jsonValue);
                XposedHelpers.setStaticObjectField(Build.class, "DEVICE", jsonValue);
                buildValueHashMap.put("ro.product.device", jsonValue);
                buildValueHashMap.put("ro.product.model" , jsonValue);
            }
            if (xposedPackageJson.has("android.os.Build.ro.serialno")) {
                XposedHelpers.setStaticObjectField(Build.class, "SERIAL", xposedPackageJson.getString("android.os.Build.ro.serialno"));
                buildValueHashMap.put("ro.serialno", xposedPackageJson.getString("android.os.Build.ro.serialno"));
            }
            if (xposedPackageJson.has("android.os.Build.VERSION.RELEASE")) {
                XposedHelpers.setStaticObjectField(Build.VERSION.class, "RELEASE", xposedPackageJson.getString("android.os.Build.VERSION.RELEASE"));
            }
            XposedBridge.hookAllMethods(XposedHelpers.findClass("android.os.SystemProperties", loadPackageParam.classLoader), "get", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    super.afterHookedMethod(methodHookParam);
                    if (buildValueHashMap.containsKey(methodHookParam.args[0].toString())) {
                        methodHookParam.setResult(buildValueHashMap.get(methodHookParam.args[0].toString()));
                    }
                }
            });

            if (xposedPackageJson.has("android.os.SystemProperties.android_id")) {
                XposedBridge.hookAllMethods(android.provider.Settings.System.class, "getString", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        super.afterHookedMethod(methodHookParam);
                        if (methodHookParam.args.length > 1 && "android_id".equals(methodHookParam.args[1].toString())) {
                            methodHookParam.setResult(xposedPackageJson.getString("android.os.SystemProperties.android_id"));
                        }
                    }
                });
            }

            if (xposedPackageJson.has("android.telephony.TelephonyManager.getLine1Number")) {
                XposedBridge.hookAllMethods(TelephonyManager.class, "getLine1Number", new MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getLine1Number")));
            }
            if (xposedPackageJson.has("android.telephony.TelephonyManager.getDeviceId")) {
                XposedBridge.hookAllMethods(TelephonyManager.class, "getDeviceId", new MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getDeviceId")));
            }
            if (xposedPackageJson.has("android.telephony.TelephonyManager.getSubscriberId")) {
                XposedBridge.hookAllMethods(TelephonyManager.class, "getSubscriberId", new MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getSubscriberId")));
            }
            if (xposedPackageJson.has("android.telephony.TelephonyManager.getSimOperator")) {
                XposedBridge.hookAllMethods(TelephonyManager.class, "getSimOperator", new MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getSimOperator")));
            }
            if (xposedPackageJson.has("android.telephony.TelephonyManager.getSimCountryIso")) {
                XposedBridge.hookAllMethods(TelephonyManager.class, "getSimCountryIso", new MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getSimCountryIso")));
            }
            if (xposedPackageJson.has("android.telephony.TelephonyManager.getSimOperatorName")) {
                XposedBridge.hookAllMethods(TelephonyManager.class, "getSimOperatorName", new MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getSimOperatorName")));
            }
            if (xposedPackageJson.has("android.telephony.TelephonyManager.getSimSerialNumber")) {
                XposedBridge.hookAllMethods(TelephonyManager.class, "getSimSerialNumber", new MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getSimSerialNumber")));
            }
            if (xposedPackageJson.has("android.telephony.TelephonyManager.getSimState")) {
                XposedBridge.hookAllMethods(TelephonyManager.class, "getSimState", new MethodHookValue(xposedPackageJson.getInt("android.telephony.TelephonyManager.getSimState")));
            }

            if (xposedPackageJson.has("android.net.wifi.WifiInfo.getSSID")) {
                XposedBridge.hookAllMethods(WifiInfo.class, "getSSID", new MethodHookValue(xposedPackageJson.getString("android.net.wifi.WifiInfo.getSSID")));
            }
            if (xposedPackageJson.has("android.net.wifi.WifiInfo.getBSSID")) {
                XposedBridge.hookAllMethods(WifiInfo.class, "getBSSID", new MethodHookValue(xposedPackageJson.getString("android.net.wifi.WifiInfo.getBSSID")));
            }
            if (xposedPackageJson.has("android.net.wifi.WifiInfo.getMacAddress")) {
                XposedBridge.hookAllMethods(WifiInfo.class, "getMacAddress", new MethodHookValue(xposedPackageJson.getString("android.net.wifi.WifiInfo.getMacAddress")));
            }

            if (xposedPackageJson.has("android.content.res.language") || xposedPackageJson.has("android.content.res.display.dpi")) {
                XposedBridge.hookAllMethods(Resources.class, "updateConfiguration", new UpdateConfiguration(loadPackageParam, xposedPackageJson));
            }

        }
    }

    private class MethodHookValue extends XC_MethodHook {
        private final Object value;

        private MethodHookValue(Object value) {
            this.value = value;
        }

        @Override
        protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
            super.afterHookedMethod(methodHookParam);
            methodHookParam.setResult(value);
        }
    }

    private class UpdateConfiguration extends XC_MethodHook {

        private final XC_LoadPackage.LoadPackageParam loadPackageParam;
        private final JSONObject                      xposedPackageJson;

        public UpdateConfiguration(XC_LoadPackage.LoadPackageParam loadPackageParam, JSONObject xposedPackageJson)  {
            this.loadPackageParam = loadPackageParam;
            this.xposedPackageJson = xposedPackageJson;
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
            super.beforeHookedMethod(methodHookParam);
            Configuration configuration = null;
            if (methodHookParam.args[0] != null) {
                configuration = new Configuration((Configuration) methodHookParam.args[0]);
            }
            if (configuration == null)
                return;

            // 拦截语言
            if (xposedPackageJson.has("android.content.res.language")) {
                String[] localeParts = xposedPackageJson.getString("android.content.res.language").split("_");
                if (localeParts.length > 1) {
                    String language = localeParts[0];
                    String region   = localeParts.length >= 2 ? localeParts[1] : "";
                    String variant  = localeParts.length >= 3 ? localeParts[2] : "";

                    Locale locale = new Locale(language, region, variant);
                    Locale.setDefault(locale);
                    configuration.locale = locale;
                    if (Build.VERSION.SDK_INT >= 17) {
                        configuration.setLayoutDirection(locale);
                    }
                }
            }

            // 拦截DPI
            if (xposedPackageJson.has("android.content.res.display.dpi")) {
                int dpi = xposedPackageJson.getInt("android.content.res.display.dpi");
                if (dpi > 0) {
                    DisplayMetrics displayMetrics = null;
                    if (methodHookParam.args[1] != null) {
                        displayMetrics = new DisplayMetrics();
                        displayMetrics.setTo((DisplayMetrics) methodHookParam.args[1]);
                        methodHookParam.args[1] = displayMetrics;
                    }else{
                        displayMetrics = ((Resources)(methodHookParam.thisObject)).getDisplayMetrics();
                    }
                    if (displayMetrics != null) {
                        displayMetrics.density = dpi / 160f;
                        displayMetrics.densityDpi = dpi;
                        if(Build.VERSION.SDK_INT >= 17) {
                            XposedHelpers.setIntField(configuration, "densityDpi", dpi);
                        }
                    }
                }
            }
            methodHookParam.args[0] = configuration;
        }
    }

}
