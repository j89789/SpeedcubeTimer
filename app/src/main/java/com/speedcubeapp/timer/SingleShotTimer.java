package com.speedcubeapp.timer;


import android.os.Handler;

public class SingleShotTimer {

    private Handler handler = new Handler();
    private TimeoutLister timeoutLister;
    int interval = 1000;

    public void setTimeoutLister(TimeoutLister timeoutLister) {
        this.timeoutLister = timeoutLister;
    }


    public void setInterval(int interval) {
        this.interval = interval;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (timeoutLister != null) {
                timeoutLister.onTimeout();
            }
        }
    };


    public void start(){
        restart();
    }

    public void start(int timeout){
        interval = timeout;
        restart();
    }

    public void restart(){
        stop();
        handler.postDelayed(runnable, interval);
    }

    public void stop(){
        handler.removeCallbacks(runnable);
    }

    interface TimeoutLister{
         void onTimeout();
    }
}
