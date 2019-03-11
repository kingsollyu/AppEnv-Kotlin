/*
 * Copyright © 2017 Sollyu <https://www.sollyu.com/>
 *
 * Everyone is permitted to copy and distribute verbatim copies of this license document, but changing it is not allowed.
 *
 * This version of the GNU Lesser General Public License incorporates the terms and conditions of version 3 of the GNU General Public License, supplemented by the additional permissions listed below.
 */

package com.sollyu.android.appenv.commons;

import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.elvishew.xlog.XLog;
import com.sollyu.android.appenv.BuildConfig;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
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
            XposedBridgeHookAllMethods(XposedHelpers.findClass("com.sollyu.android.appenv.commons.Application", loadPackageParam.classLoader), "isXposedWork", new MethodHookValue(true));
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
        } while (false);

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

        JSONObject xposedUserJsonObject = null;
        JSONObject xposedAllJsonObject = null;
        JSONObject xposedPackageJsonObject = null;

        if (xposedSettingsJson.has("hook.model.user")) {
            xposedUserJsonObject = xposedSettingsJson.getJSONObject("hook.model.user");
        }
        if (xposedSettingsJson.has("hook.model.all")) {
            xposedAllJsonObject = xposedSettingsJson.getJSONObject("hook.model.all");
        }
        if (xposedSettingsJson.has(loadPackageParam.packageName)) {
            xposedPackageJsonObject = xposedSettingsJson.getJSONObject(loadPackageParam.packageName);
        }

        final JSONObject xposedPackageJson = mergeJson(loadPackageParam, xposedPackageJsonObject, xposedUserJsonObject, xposedAllJsonObject);

        Log.d(TAG, "===================================");
        Log.d(TAG, xposedPackageJson.toString());

        /* 拦截制定包名 */
        final HashMap<String, Object> buildValueHashMap = new HashMap<>();

        if (xposedPackageJson.has("android.os.Build.ro.product.manufacturer")) {
            String jsonValue = xposedPackageJson.getString("android.os.Build.ro.product.manufacturer");
            XposedHelpers.setStaticObjectField(Build.class, "MANUFACTURER", jsonValue);
            XposedHelpers.setStaticObjectField(Build.class, "PRODUCT", jsonValue);
            XposedHelpers.setStaticObjectField(Build.class, "BRAND", jsonValue);
            buildValueHashMap.put("ro.product.manufacturer", jsonValue);
            buildValueHashMap.put("ro.product.brand", jsonValue);
            buildValueHashMap.put("ro.product.name", jsonValue);
        }
        if (xposedPackageJson.has("android.os.Build.ro.product.model")) {
            String jsonValue = xposedPackageJson.getString("android.os.Build.ro.product.model");
            XposedHelpers.setStaticObjectField(Build.class, "MODEL", jsonValue);
            XposedHelpers.setStaticObjectField(Build.class, "DEVICE", jsonValue);
            buildValueHashMap.put("ro.product.device", jsonValue);
            buildValueHashMap.put("ro.product.model", jsonValue);
        }
        if (xposedPackageJson.has("android.os.Build.ro.serialno")) {
            XposedHelpers.setStaticObjectField(Build.class, "SERIAL", xposedPackageJson.getString("android.os.Build.ro.serialno"));
            buildValueHashMap.put("ro.serialno", xposedPackageJson.getString("android.os.Build.ro.serialno"));
        }
        if (xposedPackageJson.has("android.os.Build.VERSION.RELEASE")) {
            XposedHelpers.setStaticObjectField(Build.VERSION.class, "RELEASE", xposedPackageJson.getString("android.os.Build.VERSION.RELEASE"));
        }
        XposedBridgeHookAllMethods(XposedHelpers.findClass("android.os.SystemProperties", loadPackageParam.classLoader), "get", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                super.afterHookedMethod(methodHookParam);
                if (buildValueHashMap.containsKey(methodHookParam.args[0].toString())) {
                    methodHookParam.setResult(buildValueHashMap.get(methodHookParam.args[0].toString()));
                }
            }
        });

        if (xposedPackageJson.has("android.os.SystemProperties.android_id")) {
            XposedBridgeHookAllMethods(android.provider.Settings.System.class, "getString", new XC_MethodHook() {
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
            XposedBridgeHookAllMethods(TelephonyManager.class, "getLine1Number", new MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getLine1Number")));
        }
        if (xposedPackageJson.has("android.telephony.TelephonyManager.getDeviceId")) {
            XposedBridgeHookAllMethods(TelephonyManager.class, "getDeviceId", new MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getDeviceId")));
        }
        if (xposedPackageJson.has("android.telephony.TelephonyManager.getSubscriberId")) {
            XposedBridgeHookAllMethods(TelephonyManager.class, "getSubscriberId", new MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getSubscriberId")));
        }
        if (xposedPackageJson.has("android.telephony.TelephonyManager.getSimOperator")) {
            XposedBridgeHookAllMethods(TelephonyManager.class, "getSimOperator", new MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getSimOperator")));
        }
        if (xposedPackageJson.has("android.telephony.TelephonyManager.getSimCountryIso")) {
            XposedBridgeHookAllMethods(TelephonyManager.class, "getSimCountryIso", new MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getSimCountryIso")));
        }
        if (xposedPackageJson.has("android.telephony.TelephonyManager.getSimOperatorName")) {
            XposedBridgeHookAllMethods(TelephonyManager.class, "getSimOperatorName", new MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getSimOperatorName")));
        }
        if (xposedPackageJson.has("android.telephony.TelephonyManager.getSimSerialNumber")) {
            XposedBridgeHookAllMethods(TelephonyManager.class, "getSimSerialNumber", new MethodHookValue(xposedPackageJson.getString("android.telephony.TelephonyManager.getSimSerialNumber")));
        }
        if (xposedPackageJson.has("android.telephony.TelephonyManager.getSimState")) {
            XposedBridgeHookAllMethods(TelephonyManager.class, "getSimState", new MethodHookValue(xposedPackageJson.getInt("android.telephony.TelephonyManager.getSimState")));
        }

        if (xposedPackageJson.has("android.net.NetworkInfo.getType")) {
            String networkType = xposedPackageJson.getString("android.net.NetworkInfo.getType");
            if (networkType.equalsIgnoreCase("wifi")) {
                XposedBridgeHookAllMethods(NetworkInfo.class, "getType", new MethodHookValue(1));
            } else {
                XposedBridgeHookAllMethods(NetworkInfo.class, "getType", new MethodHookValue(0));
                XposedBridgeHookAllMethods(NetworkInfo.class, "getSubtype", new MethodHookValue(xposedPackageJson.getInt("android.net.NetworkInfo.getType")));
            }
        }
        if (xposedPackageJson.has("android.net.wifi.WifiInfo.getSSID")) {
            XposedBridgeHookAllMethods(WifiInfo.class, "getSSID", new MethodHookValue(xposedPackageJson.getString("android.net.wifi.WifiInfo.getSSID")));
        }
        if (xposedPackageJson.has("android.net.wifi.WifiInfo.getBSSID")) {
            XposedBridgeHookAllMethods(WifiInfo.class, "getBSSID", new MethodHookValue(xposedPackageJson.getString("android.net.wifi.WifiInfo.getBSSID")));
        }
        if (xposedPackageJson.has("android.net.wifi.WifiInfo.getMacAddress")) {
            XposedBridgeHookAllMethods(WifiInfo.class, "getMacAddress", new MethodHookValue(xposedPackageJson.getString("android.net.wifi.WifiInfo.getMacAddress")));
        }

        if (xposedPackageJson.has("android.content.res.language") || xposedPackageJson.has("android.content.res.display.dpi")) {
            XposedBridgeHookAllMethods(Resources.class, "updateConfiguration", new UpdateConfiguration(loadPackageParam, xposedPackageJson));
        }

    }

    private void XposedBridgeHookAllMethods(Class<?> hookClass, String methodName, XC_MethodHook callback) {
        try {
            XposedBridge.hookAllMethods(hookClass, methodName, callback);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
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
        private final JSONObject xposedPackageJson;

        private UpdateConfiguration(XC_LoadPackage.LoadPackageParam loadPackageParam, JSONObject xposedPackageJson) {
            this.loadPackageParam = loadPackageParam;
            this.xposedPackageJson = xposedPackageJson;
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
            Configuration configuration = (Configuration) methodHookParam.args[0];
            if (configuration == null)
                return;

            // 拦截语言
            if (xposedPackageJson.has("android.content.res.language") && !xposedPackageJson.getString("android.content.res.language").isEmpty()) {
                String[] localeParts = xposedPackageJson.getString("android.content.res.language").split("_");
                if (localeParts.length > 1) {
                    String language = localeParts[0];
                    String region = localeParts[1];
                    String variant = localeParts.length >= 3 ? localeParts[2] : "";

                    Locale locale = new Locale(language, region, variant);
                    Locale.setDefault(locale);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        configuration.setLayoutDirection(locale);
                        configuration.setLocale(locale);
                    } else {
                        configuration.locale = locale;
                    }
                }
            }

            // 拦截DPI
            if (xposedPackageJson.has("android.content.res.display.dpi")) {
                int dpi = xposedPackageJson.getInt("android.content.res.display.dpi");
                if (dpi > 0) {
                    DisplayMetrics displayMetrics = (DisplayMetrics) methodHookParam.args[1];
                    if (displayMetrics != null) {
                        displayMetrics.density = dpi / 160f;
                        displayMetrics.densityDpi = dpi;
                        if (Build.VERSION.SDK_INT >= 17) {
                            XposedHelpers.setIntField(configuration, "densityDpi", dpi);
                        }
                    }
                }
            }
            methodHookParam.args[0] = configuration;
        }
    }

    private JSONObject mergeJson(XC_LoadPackage.LoadPackageParam loadPackageParam, JSONObject packageJsonObject, JSONObject userJsonObject, JSONObject allJsonObject) {
        JSONObject resultJsonObject = new JSONObject();
        String[] jsonKey = {
                "android.os.Build.ro.product.manufacturer",
                "android.os.Build.ro.product.model",
                "android.os.Build.ro.serialno",
                "android.os.Build.VERSION.RELEASE",
                "android.os.SystemProperties.android_id",
                "android.telephony.TelephonyManager.getLine1Number",
                "android.telephony.TelephonyManager.getDeviceId",
                "android.telephony.TelephonyManager.getSubscriberId",
                "android.telephony.TelephonyManager.getSimOperator",
                "android.telephony.TelephonyManager.getSimCountryIso",
                "android.telephony.TelephonyManager.getSimOperatorName",
                "android.telephony.TelephonyManager.getSimSerialNumber",
                "android.telephony.TelephonyManager.getSimState",
                "android.net.NetworkInfo.getType",
                "android.net.wifi.WifiInfo.getSSID",
                "android.net.wifi.WifiInfo.getBSSID",
                "android.net.wifi.WifiInfo.getMacAddress",
                "android.content.res.language",
                "android.content.res.display.dpi"
        };
        for (String itemName : jsonKey) {
            try {
                String itemValue = mergeJsonItem(loadPackageParam, itemName, packageJsonObject, userJsonObject, allJsonObject);
                if (itemValue != null && !itemValue.isEmpty()) {
                    resultJsonObject.put(itemName, itemValue);
                }
            } catch (JSONException e) {
                resultJsonObject.remove(itemName);
            }
        }
        return resultJsonObject;
    }

    private String mergeJsonItem(XC_LoadPackage.LoadPackageParam loadPackageParam, String itemName, JSONObject packageJsonObject, JSONObject userJsonObject, JSONObject allJsonObject) throws JSONException {
        if (packageJsonObject != null && packageJsonObject.has(itemName)) {
            return packageJsonObject.getString(itemName);
        }

        boolean isSystemApplication = ((loadPackageParam.appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        if (!isSystemApplication && userJsonObject != null && userJsonObject.has(itemName)) {
            return userJsonObject.getString(itemName);
        }

        if (allJsonObject != null && allJsonObject.has(itemName)) {
            return allJsonObject.getString(itemName);
        }
        return null;
    }
}
