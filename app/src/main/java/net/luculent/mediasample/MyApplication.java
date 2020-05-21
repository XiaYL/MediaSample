package net.luculent.mediasample;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * author xiayanlei
 * date 2020/5/21
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "3a2c6b11aa", true);
    }
}
