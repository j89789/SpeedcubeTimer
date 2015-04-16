package com.example.jonas.speedcubetimer;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Application
 */
public class SpeedcubeApplication extends Application {

    private SpeedcubeTimer speedcubeTimer = new SpeedcubeTimer();
    TimeSession timeSession = new TimeSession();

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
