package com.speedcubeapp.timer;


import java.util.ArrayList;

public abstract class Cube extends Puzzle {

    protected String scrambleToString(ArrayList<Movement> moves) {
        String scramble = "";

        for (int i = 0; i < moves.size(); i++) {
            scramble += moves.get(i).toString() + " ";
        }
        return scramble;
    }

    enum Face {right, left, up, down, front, back}

    enum Direction {clockwise, counterclockwise, halfTurn}

    String generateScramble(int count, int layer) {
        ArrayList<Movement> moves = new ArrayList<>();

        for (int i = 0; i < count; i++) {

            Movement randomMove;

            while (true) {

                Face randomFace = Face.values()[random.nextInt(Face.values().length)];
                Direction randomDirection = Direction.values()[random.nextInt(Direction.values().length)];

                int randomLayer = 1;

                if(layer != 1){randomLayer = 1 + random.nextInt(layer - 1);}

                randomMove = new Movement(randomFace, randomDirection, layer);

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

                    if (randomMove.face.equals(move1.face)) {
                        isValid = false; // don't allow L L, U' U, F F', R R2,

                    } else if (move2 != null) {

                        if (randomMove.face.equals(move2.face) && randomMove.isOppositeFace(move1.face)) {

                            isValid = false; // don't allow F B F, R L2 R, U F U2
                        }
                    }
                }

                if(isValid){
                    break;
                }
            }

            moves.add(randomMove);
        }

        return scrambleToString(moves);
    }

    /**
     * One Move of a scramble.
     */
    protected static class Movement {

        public Face face;
        public Direction direction;
        public int layer = 1;

        public Movement(Face face, Direction direction) {
            this.face = face;
            this.direction = direction;
        }

        public Movement(Face face, Direction direction, int layer) {
            this.face = face;
            this.direction = direction;
            this.layer = layer;
        }

        boolean isOppositeFace(Face other) {
            return face.ordinal() / 2 == other.ordinal() / 2;
        }

        private String faceToString(Face face) {
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


        @Override
        public String toString() {
            String s = faceToString(face);

            if (direction == Direction.counterclockwise) {
                s += "'";
            } else if (direction == Direction.halfTurn) {
                s += "2";
            }

            return s;
        }
    }
}
