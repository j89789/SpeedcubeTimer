package com.speedcubeapp.timer;

import android.app.Application;
import android.content.res.ColorStateList;

/**
 * Main Application
 */
public class SpeedcubeApplication extends Application {

    private SpeedcubeTimer speedcubeTimer = new SpeedcubeTimer();
    TimeSession timeSession = new TimeSession();
    public ColorStateList defaultTextColor;

    public TimeSession getTimeSession() {
        return timeSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        globalInstance = this;
    }

    public SpeedcubeTimer getSpeedcubeTimer() {
        return speedcubeTimer;
    }

    static public SpeedcubeApplication instance(){
        return globalInstance;
    }

    static private SpeedcubeApplication globalInstance;
}
