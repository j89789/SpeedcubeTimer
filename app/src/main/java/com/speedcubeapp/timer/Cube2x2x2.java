package com.speedcubeapp.timer;


import java.util.ArrayList;

public class Cube2x2x2 extends Cube {
    @Override
    String generateScramble() {
        ArrayList<Movement> moves = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            Movement randomMove;

            while (true) {

                Face randomFace = Face.values()[random.nextInt(Face.values().length)];
                Direction randomDirection = Direction.values()[random.nextInt(Direction.values().length)];

                randomMove = new Movement(randomFace, randomDirection);

                boolean isValid = true;

                Movement lastMove = null;

                if (moves.size() > 0) {
                    lastMove = moves.get(moves.size() - 1);
                }


                if (lastMove != null) {

                    if (randomMove.face.equals(lastMove.face)) {
                        isValid = false; // don't allow L L, U' U, F F', R R2,

                    } else if (randomMove.face.equals(lastMove.face)) {

                        isValid = false; // don't allow F B, R L2, U2 D2
                    }
                }

                if (isValid) {
                    break;
                }
            }

            moves.add(randomMove);
        }

        return scrambleToString(moves);
    }

    @Override
    int getImageResourceId() {
        return R.mipmap.speedcube_2x2x2;
    }

    @Override
    int getNameResourceId() {
        return R.string.puzzle_name_2x2x2;
    }

    @Override
    int getId() {
        return 20;
    }
}
