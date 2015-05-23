package com.speedcubeapp.timer;


import java.util.ArrayList;
import java.util.Random;

public class Pyraminx extends Puzzle {

    @Override
    String generateScramble() {

        ArrayList<Move> normalMoves = new ArrayList<>();

        for (int i = 0; i < 8; i++) {

            Move randomMove;

            while (true) {

                Face randomFace = Face.values()[random.nextInt(Face.values().length)];
                Direction randomDirection = Direction.values()[random.nextInt(Direction.values().length)];

                randomMove = new Move(randomFace, randomDirection);

                boolean isValid = true;

                Move lastMove = null;

                if (normalMoves.size() > 0) {
                    lastMove = normalMoves.get(normalMoves.size() - 1);
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

            normalMoves.add(randomMove);
        }

        int tipCount = 2 + random.nextInt(2);

        ArrayList<Move> tipMoves = new ArrayList<>();

        for (int i = 0; i < tipCount; i++) {

            Move randomMove;

            while (true) {

                Face randomFace = Face.values()[random.nextInt(Face.values().length)];
                Direction randomDirection = Direction.values()[random.nextInt(Direction.values().length)];

                randomMove = new Move(randomFace, randomDirection, true);

                boolean isValid = true;

                for (Move move : tipMoves) {
                    if (move.getFace() == randomFace) {
                        isValid = false;
                    }
                }

                if (isValid) {
                    break;
                }
            }

            tipMoves.add(randomMove);
        }

        for (Move move : tipMoves) {

            move.setIsTip(true);
        }

        String scramble = "";

        for (Move move : normalMoves) {
            scramble += move.toString() + " ";
        }

        for (Move move : tipMoves) {
            scramble += move.toString() + " ";
        }

        return scramble;
    }


    @Override
    int getImageResourceId() {
        return R.mipmap.pyramix;
    }

    @Override
    int getNameResourceId() {
        return R.string.puzzle_name_pyraminx;
    }

    @Override
    int getId() {
        return 10;
    }

    enum Face {right, left, up, back}

    enum Direction {clockwise, counterclockwise}

    class Move {

        @Override
        public String toString() {
            String s = "";

            if (face == Face.right) {  s = "R";
            } else if (face == Face.left) { s = "L";
            } else if (face == Face.up) { s = "U";
            } else if (face == Face.back) { s = "B";
            }

            if (direction == Direction.counterclockwise) {
                s += "'";
            }

            if (isTip) {
                s = s.toLowerCase();
            }

            return s;
        }

        public Move(Face face, Direction direction) {
            this.face = face;
            this.direction = direction;
        }

        public Move(Face face, Direction direction, boolean isTip) {

            this.face = face;
            this.direction = direction;
            this.isTip = isTip;
        }

        Face face;
        Direction direction;
        boolean isTip;

        public void setIsTip(boolean isTip) {
            this.isTip = isTip;
        }


        public Face getFace() {
            return face;
        }
    }
}
