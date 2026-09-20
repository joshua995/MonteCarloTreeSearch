
/*Bitmap
 . . . . . . . 41-35
 . . . . . . . 34-28
 . . . . . . . 27-21
 . . . . . . . 20-14
 . . . . . . . 13-7 
 . . . . . . . 6-0
 1 2 3 4 5 6 7
 */

import java.util.Random;

public class ConnectFour {
    static long player1Board = 0b000_0000_000_0000_000_000_000_0000_000_0000_000_0000L;
    static long player2Board = 0b0l;
    static int currentPlayer = 1;

    static final long BOARD_MASK = 0x3FFFFFFFFFFL;
    static final Random RANDOM = new Random();

    public static void main(String[] args) {
        displayBoard();
        for (int i = 0; i < 42; i++) {
            int[] legalMoves = getLegalMoves();
            if (legalMoves.length == 0) {
                return;
            }
            player1Board = makeMove(player1Board, legalMoves[RANDOM.nextInt(0, legalMoves.length)], false);
            displayBoard();
            System.out.println("1, " + checkWinner(player1Board));
            if (checkWinner(player1Board)) {
                return;
            }
            legalMoves = getLegalMoves();
            if (legalMoves.length == 0) {
                return;
            }
            player2Board = makeMove(player2Board, legalMoves[RANDOM.nextInt(0, legalMoves.length)], false);
            displayBoard();
            System.out.println("2, " + checkWinner(player2Board));
            if (checkWinner(player2Board)) {
                return;
            }
        }
    }

    static void displayBoard() {
        for (int i = 41; i >= 0; i--) {
            if ((player1Board & (1L << i)) != 0) {
                System.out.print("1 ");
            } else if ((player2Board & (1L << i)) != 0) {
                System.out.print("2 ");
            } else {
                System.out.print(". ");
            }

            if (i % 7 == 0) {
                System.out.println();
            }
        }
        System.out.println("-------------\n1 2 3 4 5 6 7");
    }

    static int[] getLegalMoves() {
        long legalMoveBitmap = 0l;
        // position % 7 = column
        for (int move = 0; move < 7; move++) {
            for (int i = 0; i < 6; i++) {
                // Is empty
                if ((((((player1Board | player2Board) >> i * 7) & 0b1111_111) >> move) & 1) == 0) {
                    legalMoveBitmap |= 1l << ((i * 7) + move);
                    break;
                }
            }
        }
        int[] legalMoves = new int[Long.bitCount(legalMoveBitmap)];
        for (int i = 0; i < legalMoves.length; i++) {
            legalMoves[i] = Long.numberOfTrailingZeros(legalMoveBitmap);
            legalMoveBitmap &= legalMoveBitmap - 1;
        }
        return legalMoves;
    }

    static long makeMove(long playerBoard, int move, boolean isPlayer) {
        if (isPlayer) {
            // 7 - move = index
            move = 7 - move;
            for (int i = 0; i < 6; i++) {
                // Is empty
                // System.out.println((i * 7) + move);
                if ((((((player1Board | player2Board) >> i * 7) & 0b1111_111) >> move) & 1) == 0) {
                    playerBoard |= 1l << ((i * 7) + move);
                    break;
                }
                if (i == 5) {
                    System.out.println("IllegalMove");
                }
            }
        } else {
            playerBoard |= 1l << move;
        }
        return playerBoard;
    }

    static boolean checkWinner(long playerBoard) {
        long temp = playerBoard;
        long[] rows = {
                (playerBoard & 0b1111_111),
                ((playerBoard >> 7) & 0b1111_111),
                ((playerBoard >> 14) & 0b1111_111),
                ((playerBoard >> 21) & 0b1111_111),
                ((playerBoard >> 28) & 0b1111_111),
                ((playerBoard >> 35) & 0b1111_111),
        };

        // Check horizontal connect 4
        for (int i = 0; i < 6; i++) {
            long horizontal = playerBoard & (playerBoard >> 1);
            if ((horizontal & (horizontal >> 2)) != 0) {
                return true;
            }

            if (i < 3) {
                // Check vertical and diagonal connect 4
                if ((((playerBoard >>> i * 7) & (playerBoard >>> 7 + (i * 7)) & (playerBoard >>> 14 + (i * 7))
                        & (playerBoard >>> 21 + (i * 7))) != 0)
                        || ((rows[i] & (rows[i + 1] >> 1) & (rows[i + 2] >> 2) & (rows[i + 3] >> 3)) != 0)
                        || ((rows[i] & (rows[i + 1] << 1) & (rows[i + 2] << 2) & (rows[i + 3] << 3)) != 0)) {
                    return true;
                }
            }
        }

        return false;
    }
}
