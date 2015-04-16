package com.example.jonas.speedcubetimer;


public class Time {

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

    static public String toStringMs(long timeMs) {
        return Time.toString(timeMs, 3);
    }

    /**
     * Convert a millisecond time in to a String e.g 3:02.642.
     *
     * @param timeMs   Time in milliseconds
     * @param decimals Count of seconds decimals
     */
    static public String toString(long timeMs, int decimals) {
        long milliseconds = Math.abs(timeMs);
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        milliseconds %= 1000;
        seconds %= 60;
        minutes %= 60;

        String millisecondsString = "";
        String secondsString = "";
        String minutesString = "";

        if (decimals == 2) {
            millisecondsString = String.format("%02d", (milliseconds / 10) % 100);
        } else if (decimals == 1) {
            millisecondsString = "" + ((milliseconds / 100) % 10);
        } else if (decimals == 0) {
        } else {
            millisecondsString = String.format("%03d", milliseconds);
        }


        if (minutes != 0) {
            secondsString = String.format("%02d", seconds);
            minutesString = "" + minutes;
        } else {
            secondsString = "" + seconds;
        }

        String s = timeMs < 0 ? "- " : "";

        if (!minutesString.isEmpty()) {
            s += minutesString + ":";
        }

        s += secondsString;

        if (!millisecondsString.isEmpty()) {
            s += "." + millisecondsString;
        }

        return s;
    }
}
