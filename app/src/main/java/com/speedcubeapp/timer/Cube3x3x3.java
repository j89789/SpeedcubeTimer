package com.speedcubeapp.timer;


public class Cube3x3x3 extends Cube {

    public String generateScramble() {
        return scrambleToString(generateMoves(21));
    }

    @Override
    int getImageResourceId() {
        return R.mipmap.ic_launcher;
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
