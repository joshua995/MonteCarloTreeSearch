
/*Bitmap
 . . . . . . . 41-35
 . . . . . . . 34-28
 . . . . . . . 27-21
 . . . . . . . 20-14
 . . . . . . . 13-7 
 . . . . . . . 6-0
 */

public class ConnectFour {
    static long player1Board = 0b000_0000_000_1000_000_1100_001_0110_000_0001_100_0001L;
    static long player2Board = 0b0;

    static final long BOARD_MASK = 0x3FFFFFFFFFFL;

    public static void main(String[] args) {
        displayBoard();
        System.out.println(checkWinner(player1Board));
    }

    static void displayBoard() {
        for (int i = 41; i >= 0; i--) {
            if ((player1Board & (1L << i)) != 0) {
                System.out.print("X");
            } else if ((player2Board & (1L << i)) != 0) {
                System.out.print("O");
            } else {
                System.out.print(".");
            }

            if (i % 7 == 0) {
                System.out.println();
            }
        }
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
            temp = temp >> i * 7;
            long next = temp & ((temp >> Long.numberOfTrailingZeros(temp)) >> 1);
            if ((next & (next >> 2)) != 0) {
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
