
/*Bitmap
 . . . . . . . 41-35
 . . . . . . . 34-28
 . . . . . . . 27-21
 . . . . . . . 20-14
 . . . . . . . 13-7 
 . . . . . . . 6-0
 */

public class ConnectFour {
    static long player1Board = 0b000_0010_000_0001_000_0011_000_0011_000_0010_000_0111l;
    static long player2Board = 0l;

    static final long BOARD_MASK = 0x3FFFFFFFFFFl;

    public static void main(String[] args) {
        displayBoard();
        // System.out.println(
        // String.format("%7s", Long.toBinaryString((player1Board >> 35) &
        // 0b1111_111)).replace(" ", "0"));
        // System.out.println(
        // String.format("%7s", Long.toBinaryString((player1Board >> 28) &
        // 0b1111_111)).replace(" ", "0"));
        // System.out.println(
        // String.format("%7s", Long.toBinaryString((player1Board >> 21) &
        // 0b1111_111)).replace(" ", "0"));
        // System.out.println(
        // String.format("%7s", Long.toBinaryString((player1Board >> 14) &
        // 0b1111_111)).replace(" ", "0"));
        // System.out.println(
        // String.format("%7s", Long.toBinaryString((player1Board >> 7) &
        // 0b1111_111)).replace(" ", "0"));

        // System.out.println(
        // String.format("%7s", Long.toBinaryString((player1Board) &
        // 0b1111_111)).replace(" ", "0"));
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

        // Check horizontal connect 4
        for (int i = 0; i < 6; i++) {
            temp = temp >> i * 7;
            long next = temp & ((temp >> Long.numberOfTrailingZeros(temp)) >> 1);
            if ((next & (next >> 2)) != 0) {
                return true;
            }

            // Check vertical connect 4
            if (i < 3) {
                if (((playerBoard >>> i * 7) & (playerBoard >>> 7 + (i * 7)) & (playerBoard >>> 14 + (i * 7))
                        & (playerBoard >>> 21 + (i * 7))) != 0) {
                    return true;
                }
            }
        }

        // Check vertical connect 4
        return false;
    }
}
