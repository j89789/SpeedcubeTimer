package com.example.jonas.speedcubetimer;


public class SolvingTime {

    private long timeMs = 0;
    private Type type = Type.valid;

    public long getTimeMs() {
        return timeMs;
    }

    public void setTimeMs(long timeMs) {
        this.timeMs = timeMs;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    enum Type {valid, plus2, DNF}


}
