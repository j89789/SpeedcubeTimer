package com.example.jonas.speedcubetimer;

/**
 * Converting times in milliseconds to a readable String
 */
public class Time {

    static public String toNormalString(long timeMs) {
        long milliseconds = timeMs;
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        String millisecondsString = String.format("%03d", milliseconds % 1000);
        String secondsString = String.format("%02d", seconds % 60);
        String minutesString = String.format("%d", minutes % 60);

        return "" + minutesString + ":" + secondsString + ":" + millisecondsString;
    }
}
