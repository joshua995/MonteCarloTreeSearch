
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

class MCTSNode extends ConnectFour {
    private long player1Board;
    private long player2Board;
    private MCTSNode parent;
    private int action;
    private boolean isPlayerOne = true;
    private MCTSNode[] children = new MCTSNode[9];
    private int childCount;
    private int visits;
    private double wins;
    private long untriedActions;

    public MCTSNode(long player1Board, long player2Board, MCTSNode parent, int action, boolean isPlayerOne) {
        this.player1Board = player1Board;
        this.player2Board = player2Board;
        this.parent = parent;
        this.action = action;
        this.isPlayerOne = isPlayerOne;
        this.untriedActions = getLegalMoveBitmap(player1Board, player2Board);
    }

    boolean isTerminal() {
        return checkWinner(this.player1Board, this.player2Board) != 0
                || getLegalMoveBitmap(player1Board, player2Board) == 0; // Ensure there are legal moves available
    }

    boolean isFullyExpanded() {
        return untriedActions == 0;
    }

    public MCTSNode expand() {
        int action = Long.numberOfTrailingZeros(untriedActions);

        // Remove action
        untriedActions &= untriedActions - 1;

        int playerToMove = getCurrentPlayer(this.player1Board, this.player2Board);

        MCTSNode child;
        if (playerToMove == 1) {
            long newState = makeMove(this.player1Board, this.player2Board, action, false);
            child = new MCTSNode(newState, this.player2Board, this, action, true);
            this.children[this.childCount++] = child;
        } else {
            long newState = makeMove(this.player2Board, this.player1Board, action, false);
            child = new MCTSNode(this.player1Board, newState, this, action, false);
            this.children[this.childCount++] = child;
        }

        return child;
    }

    public MCTSNode bestChild(double c) {
        MCTSNode best = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < childCount; i++) {
            MCTSNode child = children[i];

            if (child.visits == 0)
                return child;

            double score = (double) child.wins / child.visits
                    + c * Math.sqrt(Math.log(visits) / child.visits);

            if (score > bestScore) {
                bestScore = score;
                best = child;
            }
        }
        return best;
    }

    public int rollout() {
        long np1State = this.player1Board;
        long np2State = this.player2Board;
        int player = getCurrentPlayer(np1State, np2State);
        while (true) {
            int winner = checkWinner(np1State, np2State);
            if (winner != 0) {
                return winner;
            }
            int[] legalMoves = getLegalMoves(np1State, np2State);
            if (legalMoves.length == 0) {
                return 0;
            }
            if (player == 1) {
                np1State = makeMove(np1State, np2State, legalMoves[RANDOM.nextInt(0, legalMoves.length)], false);
            } else {
                np2State = makeMove(np2State, np1State, legalMoves[RANDOM.nextInt(0, legalMoves.length)], false);
            }
            player = 3 - player;
        }
    }

    public void backpropagate(int winner) {
        for (MCTSNode node = this; node != null; node = node.parent) {
            node.visits++;
            if (winner != 0) {
                node.wins += winner == (node.isPlayerOne ? 1 : 2) ? 1 : -1;
            }
        }
    }

    public static int mctsSearch(long rootP1State, long rootP2State, int iterations) {
        MCTSNode root = new MCTSNode(rootP1State, rootP2State, null, -1, false);
        for (int i = 0; i < iterations; i++) {
            MCTSNode node = root;
            while (!node.isTerminal() && node.isFullyExpanded()) {
                node = node.bestChild(2 * 1.4);
            }
            if (!node.isTerminal() && !node.isFullyExpanded()) {
                node = node.expand();
            }
            // System.out.println("expand");
            int winner = node.rollout();
            node.backpropagate(winner);
        }
        MCTSNode best = root.children[0];
        for (MCTSNode child : root.children) {
            if (child == null) {
                break;
            }
            if (child.visits > best.visits) {
                best = child;
            }
        }
        return best.action;
    }
}

public class ConnectFour {
    static long player1Board = 0b000_0000_000_0000_000_000_000_0000_000_0000_000_0000L;
    static long player2Board = 0l;
    static int currentPlayer = 1;
    static int move = -1;
    static int[] winCounter = new int[2];

    static final Random RANDOM = new Random();
    static final int GAMES = 100000;

    public static void main(String[] args) {
        // displayBoard();
        for (int rounds = 0; rounds < GAMES; rounds++) {
            if (rounds % 1000 == 0) {
                System.out.println("P1 wins: " + winCounter[0]);
                System.out.println("P2 wins: " + winCounter[1]);
                System.out.println("Draws: " + (rounds - (winCounter[0] + winCounter[1])));
            }
            player1Board = 0b000_0000_000_0000_000_000_000_0000_000_0000_000_0000L;
            player2Board = 0l;
            currentPlayer = 1;
            move = -1;
            for (int i = 0; i < 42; i++) {
                if (currentPlayer == 1) {
                    move = MCTSNode.mctsSearch(player1Board, player2Board, 500);
                    player1Board = makeMove(player1Board, player2Board, move, false);
                } else {
                    // move = MCTSNode.mctsSearch(player1Board, player2Board, 10000);
                    // player2Board = makeMove(player2Board, player1Board, move, false);
                    int[] legalMoves = getLegalMoves(player1Board, player2Board);
                    if (legalMoves.length == 0) {
                        return;
                    }
                    player2Board = makeMove(player2Board, player1Board, legalMoves[RANDOM.nextInt(0,
                            legalMoves.length)], false);
                }
                if (checkWinner(player1Board, player2Board) != 0) {
                    // displayBoard();
                    winCounter[checkWinner(player1Board, player2Board) - 1]++;
                    // System.out.println("winner " + checkWinner(player1Board, player2Board));
                    break;
                }
                currentPlayer = 3 - currentPlayer;
                // int[] legalMoves = getLegalMoves(player1Board, player2Board);
                // if (legalMoves.length == 0) {
                // return;
                // }
                // player1Board = makeMove(player1Board, legalMoves[RANDOM.nextInt(0,
                // legalMoves.length)], false);
                // displayBoard();
                // System.out.println("1, " + checkWinner(player1Board));
                // if (checkWinner(player1Board)) {
                // return;
                // }
                // legalMoves = getLegalMoves(player1Board, player2Board);
                // if (legalMoves.length == 0) {
                // return;
                // }
                // player2Board = makeMove(player2Board, legalMoves[RANDOM.nextInt(0,
                // legalMoves.length)], false);
                // displayBoard();
                // System.out.println("2, " + checkWinner(player2Board));
                // if (checkWinner(player2Board)) {
                // return;
                // }
            }
        }
        System.out.println("P1 wins: " + winCounter[0]);
        System.out.println("P2 wins: " + winCounter[1]);
        System.out.println("Draws: " + (GAMES - (winCounter[0] + winCounter[1])));
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

    static int[] getLegalMoves(long p1Board, long p2Board) {
        long legalMoveBitmap = 0l;
        // position % 7 = column
        for (int move = 0; move < 7; move++) {
            for (int i = 0; i < 6; i++) {
                // Is empty
                if ((((((p1Board | p2Board) >> i * 7) & 0b1111_111) >> move) & 1) == 0) {
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

    static long getLegalMoveBitmap(long p1Board, long p2Board) {
        long legalMoveBitmap = 0l;
        // position % 7 = column
        for (int move = 0; move < 7; move++) {
            for (int i = 0; i < 6; i++) {
                // Is empty
                if ((((((p1Board | p2Board) >> i * 7) & 0b1111_111) >> move) & 1) == 0) {
                    legalMoveBitmap |= 1l << ((i * 7) + move);
                    break;
                }
            }
        }
        return legalMoveBitmap;
    }

    static long makeMove(long playerBoard, long otherPlayerBoard, int move, boolean isPlayer) {
        if (isPlayer) {
            // 7 - move = index
            move = 7 - move;
            for (int i = 0; i < 6; i++) {
                // Is empty
                // System.out.println((i * 7) + move);
                if ((((((playerBoard | otherPlayerBoard) >> i * 7) & 0b1111_111) >> move) & 1) == 0) {
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
            long temp = rows[i] & (rows[i] >> 1);
            if ((temp & (temp >> 2)) != 0) {
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

    static int checkWinner(long player1Board, long player2Board) {
        if (checkWinner(player1Board)) {
            return 1;
        } else if (checkWinner(player2Board)) {
            return 2;
        }
        return 0;
    }

    static int getCurrentPlayer(long player1Board, long player2Board) {
        return Long.bitCount(player1Board) == Long.bitCount(player2Board) ? 1 : 2;
    }
}
