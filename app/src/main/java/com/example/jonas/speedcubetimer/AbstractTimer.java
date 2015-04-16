package com.example.jonas.speedcubetimer;

import android.util.Log;

/**
 * A basic timer with start, stop and reset function.
 * <p/>
 * If you call start() the timer is running. The current time in Milliseconds is calculated
 * in the abstract function getCurrentTime(). By Calling stop the timer is not running. The timer
 * can be restart with start() or reset by reset(), A reset time is a ready time (isReady()).
 */
abstract class AbstractTimer {

    protected final ElapsedTimer elapsedTimer = new ElapsedTimer();
    private final String TAG = Timer.class.getSimpleName();
    protected long offset = 0;
    private boolean isReady = true;
    private boolean isRunning = false;

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isReady() {
        return isReady;
    }

    /**
     * @return Current Time2 in milliseconds
     */
    public abstract long getCurrentTime();

    public void start() {
        this.isRunning = true;
        this.isReady = false;
        this.elapsedTimer.start();

        Log.d(TAG, "Timer started");
    }

    public void stop() {
        this.isRunning = false;
        this.offset += this.elapsedTimer.elapsed();

        Log.d(TAG, "Timer stopped");
    }

    public void reset() {
        this.offset = 0;

        if (!this.isRunning) {
            this.isReady = true;
            Log.d(TAG, "Timer was reset");
        } else {
            this.elapsedTimer.restart();
            Log.d(TAG, "Timer reset while running");
        }
    }

    public String currentTimeToMsString() {
        return Time2.toStringMs(getCurrentTime());
    }

    public String currentToTsString() {
        return Time2.toString(this.getCurrentTime(), 1);
    }

    public String currentToHsString() {
        return Time2.toString(this.getCurrentTime(), 2);
    }
}
