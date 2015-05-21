package com.speedcubeapp.timer;


public class Cube4x4x4 extends Cube{

    @Override
    String generateScramble() {
        return generateScramble(30, 2);
    }

    @Override
    int getImageResourceId() {
        return R.mipmap.speedcube_4x4x4;
    }

    @Override
    int getNameResourceId() {
        return R.string.puzzle_name_4x4x4;
    }
}
