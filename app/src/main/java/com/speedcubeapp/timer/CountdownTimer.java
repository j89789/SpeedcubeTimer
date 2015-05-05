package com.speedcubeapp.timer;

/**
 * The time will go down form the giver up value. The time can by smaller than zero. The default
 * up time is 15 seconds.
 */
class CountdownTimer extends AbstractTimer {

    private int upTime = 15000;

    /**
     * Work only if the timer is not running.
     */
    public void setUpTime(int upTime) {
        if(!this.isRunning()) {
            this.upTime = upTime;
        }
    }

    @Override
    public long getCurrentTime() {
        return upTime - this.offset - (this.isRunning() ? elapsedTimer.elapsed() : 0);
    }
}
