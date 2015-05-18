package com.speedcubeapp.timer;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;

/**
 * Main Application
 */
public class SpeedcubeApplication extends Application {

    private SpeedcubeTimer speedcubeTimer = new SpeedcubeTimer();
    TimeSession timeSession = new TimeSession();
    public ColorStateList defaultTextColor;
    static public int versionCode;
    static public String versionName;

    public TimeSession getTimeSession() {
        return timeSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        globalInstance = this;

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public SpeedcubeTimer getSpeedcubeTimer() {
        return speedcubeTimer;
    }

    static public SpeedcubeApplication instance(){
        return globalInstance;
    }

    static private SpeedcubeApplication globalInstance;
}
