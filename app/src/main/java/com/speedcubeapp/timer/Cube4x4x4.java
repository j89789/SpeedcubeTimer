package com.speedcubeapp.timer;


import java.util.ArrayList;

public class Cube4x4x4 extends Cube{

    @Override
    String generateScramble() {

        ArrayList<Movement> moves = generateMoves(30);

        for (Movement move : moves) {

            if (random.nextInt(3) == 1) {
                move.setLayer(2);
            }
        }

        return scrambleToString(moves);
    }

    @Override
    int getImageResourceId() {
        return R.mipmap.speedcube_4x4x4;
    }

    @Override
    int getNameResourceId() {
        return R.string.puzzle_name_4x4x4;
    }

    @Override
    int getId() {
        return 40;
    }
}
