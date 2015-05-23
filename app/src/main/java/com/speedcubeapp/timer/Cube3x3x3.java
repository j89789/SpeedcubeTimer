package com.speedcubeapp.timer;


public class Cube3x3x3 extends Cube {

    public String generateScramble() {
        return generateScramble(21, 1);
    }

    @Override
    int getImageResourceId() {
        return R.mipmap.speedcube3x3x3;
    }

    @Override
    int getNameResourceId() {
        return R.string.puzzle_name_3x3x3;
    }

    @Override
    int getId() {
        return 30;
    }

}
