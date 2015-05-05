package com.speedcubeapp.timer;


import android.util.Log;
import java.util.ArrayList;
import java.util.Random;

/**
 * Generate a scramble for a 3*3*3 speedcube
 *
 */
public class ScrambleGenerator {

    enum Face {right, left, up, down, front, back}
    enum Direction {clockwise, counterclockwise, halfTurn}

    private Random random = new Random();


    public String generateScramble() {

        ArrayList<Movement> moves = new ArrayList<>();

        for (int i = 0; i < 21; i++) {

            Movement move;

            do {
                move = new Movement(
                        Face.values()[random.nextInt(Face.values().length)],
                        Direction.values()[random.nextInt(Direction.values().length)]);
            } while (!move.isValidForNext(moves));

            moves.add(move);
        }

        String scramble = "";

        for (int i = 0; i < moves.size(); i++) {
            scramble += moves.get(i).toString() + " ";
        }

        return scramble;
    }

    /**
     * One Move of a scramble.
     */
    private static class Movement {

        private Face face;
        private Direction direction;

        public Movement(Face face, Direction direction) {
            this.face = face;
            this.direction = direction;
        }

        boolean isOppositeFace(Face other) {
            return face == Face.right && other == Face.left
                    || face == Face.left && other == Face.right
                    || face == Face.up && other == Face.down
                    || face == Face.down && other == Face.up
                    || face == Face.front && other == Face.back
                    || face == Face.back && other == Face.front;
        }

        private String faceToString(Face face)
        {
            if (this.face == Face.right) {
                return "R";
            } else if (this.face == Face.left) {
                return "L";
            } else if (this.face == Face.up) {
                return "U";
            } else if (this.face == Face.down) {
                return "D";
            } else if (this.face == Face.front) {
                return "F";
            } else if (this.face == Face.back) {
                return "B";
            }

            return "";
        }

        /**
         * Check if this move can be the next of the given list
         */
        public boolean isValidForNext(ArrayList<Movement> moves) {

            boolean isValid = true;

            Movement move1 = null;
            Movement move2 = null;

            if (moves.size() > 0) {
                move1 = moves.get(moves.size() - 1);
            }

            if (moves.size() > 1) {
                move2 = moves.get(moves.size() - 2);
            }

            if (move1 != null) {

                if (face.equals(move1.face)) {
                    isValid = false; // don't allow L L, U' U, F F', R R2,

                } else if (move2 != null) {

                    if (face.equals(move2.face) && isOppositeFace(move1.face)) {

                        isValid = false; // don't allow F B F, R L2 R, U F U2
                    }
                }
            }

            return isValid;
        }

        @Override
        public String toString() {
            String s =  faceToString(face);

            if(direction == Direction.counterclockwise){
                s += "'";
            }else if(direction == Direction.halfTurn){
                s += "2";
            }

            return s;
        }
    }
}