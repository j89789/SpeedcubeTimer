package com.example.jonas.speedcubetimer;

import android.util.Log;

class Timer extends TimerBase{

    /**
     * @return Time in milliseconds since start was called.
     */
    public long getCurrentTime(){
        return this.offset + (this.isRunning() ? elapsedTimer.elapsed() : 0);
    }
}
