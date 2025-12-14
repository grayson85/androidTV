package com.pxf.fftv.plus;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import androidx.multidex.MultiDex;

import com.pxf.fftv.plus.Const;
import com.pxf.fftv.plus.bean.BaseDataBean;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.statistics.common.DeviceConfig;

public class FFTVApplication extends Application {

    public static int screenWidth = 0;
    public static int screenHeight = 0;

    public static boolean login = false;
    public static String account = "";
    public static String password = "";
    public static long vipDate = Const.ACCOUNT_NO_VIP;
    public static String token = "";
    public static String weiduo_analysis_play_url = "http://k377.cc/json/?url=";

    public static int VIP_MODE = 1;

    public static BaseDataBean baseDataBean;

    private static FFTVApplication instance;

    public static FFTVApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        /*
         * CrashReport.initCrashReport(getApplicationContext(), "62149201ee", true);
         * 
         * String channel = "fftv";
         * try {
         * ApplicationInfo appInfo =
         * getPackageManager().getApplicationInfo(getPackageName(),
         * PackageManager.GET_META_DATA);
         * if (appInfo != null && appInfo.metaData != null) {
         * channel = appInfo.metaData.getString("channel");
         * }
         * } catch (PackageManager.NameNotFoundException e) {
         * e.printStackTrace();
         * }
         * 
         * TelephonyManager telephony =
         * (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
         * 
         * UMConfigure.setLogEnabled(true);
         * if (telephony.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {
         * UMConfigure.init(this, "key", channel, UMConfigure.DEVICE_TYPE_PHONE, "");
         * } else {
         * UMConfigure.init(this, "key", channel, UMConfigure.DEVICE_TYPE_BOX, "");
         * } else {
         * UMConfigure.init(this, "key", channel, UMConfigure.DEVICE_TYPE_BOX, "");
         * }
         */

        // Initialize Base URL from Preferences
        Const.BASE_URL = com.pxf.fftv.plus.model.Model.getData().getCmsApiUrl(this);
        Const.refreshUrls(); // Update all dependent URL fields
        Log.d(Const.LOG_TAG, "Initialized Const.BASE_URL: " + Const.BASE_URL);
    }
}
