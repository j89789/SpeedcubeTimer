package com.example.jonas.speedcubetimer;

import android.util.Log;

/**
 * A simple timer with start, stop and reset function.
 *
 * If you call start() the timer is running. The current time in Milliseconds is calculated
 * in the function currentTime(). By Calling stop the timer is not running but is also not a
 * null time. rest() will reset the time to null. start() will restart the Timer.
 */
class Timer {

    private final ElapsedTimer elapsedTimer = new ElapsedTimer();
    private boolean isRunning = false;
    private boolean isNullTime = true;

    public boolean isRunning() {
        return isRunning;
    }

    private long offset = 0;

    public boolean isNullTime() {
        return isNullTime;
    }

    /**
     * @return Time in milliseconds since start was called.
     */
    private long currentTime(){
        return this.offset + (this.isRunning ? elapsedTimer.elapsed() : 0);
    }

    public void start(){
        this.isRunning = true;
        this.isNullTime = false;
        this.elapsedTimer.start();

        Log.d("Timer", "Timer started");
    }

    public void stop(){
        this.isRunning = false;
        this.offset = this.elapsedTimer.elapsed();

        Log.d("Timer", "Timer stopped");
    }

    public void reset(){
        this.offset = 0;

        if(!this.isRunning) {
            this.isNullTime = true;
            Log.d("Timer", "Timer was reset");
        }
        else
        {
            this.elapsedTimer.restart();
            Log.d("Timer", "Timer reset while running");
        }
    }

    public String currentTimeAsString(){
        long milliseconds = this.currentTime();
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        String millisecondsString = String.format("%03d", milliseconds % 1000);
        String secondsString = String.format("%02d", seconds % 60);
        String minutesString = String.format("%d", minutes % 60);

        return "" + minutesString + ":" + secondsString + ":" + millisecondsString;
    }
}
