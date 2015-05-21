package com.speedcubeapp.timer;


public class Pyraminx extends Puzzle {

    @Override
    String generateScramble() {
        return "";
    }

    @Override
    int getImageResourceId() {
        return R.mipmap.pyramix;
    }

    @Override
    int getNameResourceId() {
        return R.string.puzzle_name_pyraminx;
    }
}
