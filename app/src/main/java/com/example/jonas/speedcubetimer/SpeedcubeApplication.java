package com.example.jonas.speedcubetimer;

import android.app.Application;

/**
 * Main Application
 */
public class SpeedcubeApplication extends Application {
    SpeedcubeTimer speedcubeTimer = new SpeedcubeTimer();

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
