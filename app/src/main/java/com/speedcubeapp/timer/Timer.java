package com.speedcubeapp.timer;

class Timer extends AbstractTimer {

    /**
     * @return Time in milliseconds since start was called.
     */
    public long getCurrentTime(){
        return this.offset + (this.isRunning() ? elapsedTimer.elapsed() : 0);
    }
}
