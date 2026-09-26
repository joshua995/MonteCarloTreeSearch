public class Checkers {
    static long player1Board = 0x00000FFFl;
    static long player2Board = 0xFFF00000l;

    static boolean isPlayerOne = true;

    public static void main(String[] args) {
        displayBoard(player1Board, player2Board);
        getLegalMoves(player1Board, player2Board, isPlayerOne);
    }

    static void displayBoard(long player1Board, long player2Board) {
        for (int i = 32; i >= 0; i--) {
            if ((player1Board & (1L << i)) != 0) {
                System.out.print("1");
            } else if ((player2Board & (1L << i)) != 0) {
                System.out.print("2");
            } else if (i < 32) {
                System.out.print("e"); // Empty
            }
            if (i % 4 != 0 || (i % 8 == 0 && i < 32)) {
                System.out.print("-");
            }
            if (i % 4 == 0 && i < 32) {
                System.out.println();
            }
            if (i % 8 == 0 && i > 0) {
                System.out.print("-");
            }
        }
    }

    static long getLegalMoves(long playerBoard, long otherPlayerBoard, boolean isPlayerOne) {
        long ret = 0l;
        long board = playerBoard | otherPlayerBoard;
        if (isPlayerOne) {
            for (int i = 0; i < 7; i++) {
                long row = (playerBoard >> (i * 4)) & 0xFl;
                long nextRow = (board >> (4 + i * 4)) & 0xFL;
                // Has legal moves
                if (row != 0 && (row ^ nextRow) != 0) {
                    System.out.println("R " + Long.toBinaryString(row));
                    System.out.println("N " + Long.toBinaryString(nextRow));
                    System.out.println(i + ", " + Long.toBinaryString((row ^ nextRow)));
                }
            }
        }

        return ret;
    }

    static void makeMove(long playerBoard, long otherPlayerBoard, int move, boolean isPlayerOne) {

    }
}
