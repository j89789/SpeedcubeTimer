package com.jonas.speedcube;

class ElapsedTimer {

    private boolean isValid = false;
    private long startTime = 0;

    public void invalidate(){
        this.isValid = true;
        this.startTime = 0;
    }

    public boolean isValid() {
        return isValid;
    }

    public long elapsed(){
        if(this.isValid) {
            return System.currentTimeMillis() - this.startTime;
        }
        return 0;
    }

    public void start(){
        this.startTime = System.currentTimeMillis();
        this.isValid = true;
    }

    public void restart(){
        this.start();
    }
}
