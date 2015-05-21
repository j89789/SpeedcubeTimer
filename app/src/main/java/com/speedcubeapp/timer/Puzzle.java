package com.speedcubeapp.timer;


import java.util.Random;

public abstract class Puzzle {

    private TimeSession session = new TimeSession();
    protected Random random = new Random();

    public TimeSession getSession() {
        return session;
    }

    abstract String generateScramble();

    abstract int getImageResourceId();

    abstract int getNameResourceId();
}
