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
     * @return Current Time in milliseconds
     */
    public abstract long getCurrentTime();

    public void start() {
        this.isRunning = true;
        this.isReady = false;
        this.elapsedTimer.start();
    }

    public void stop() {
        this.isRunning = false;
        this.offset += this.elapsedTimer.elapsed();
    }

    public void reset() {
        this.offset = 0;

        if (!this.isRunning) {
            this.isReady = true;
        } else {
            this.elapsedTimer.restart();
        }
    }

}
