package tictactoe;
/*
Joshua Liu
Bitmap Tic Tac Toe with MCTS
2026-Sep-17
CPU vs CPU
*/

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class MCTSNode extends BitmapMCTSTTT {
    private int state;
    private MCTSNode parent;
    private int action;
    private boolean isPlayerOne = true;
    private MCTSNode[] children = new MCTSNode[9];
    private int childCount;
    private int visits;
    private double wins;
    private int untriedActions;

    public MCTSNode(int state, MCTSNode parent, int action, boolean isPlayerOne) {
        this.state = state;
        this.parent = parent;
        this.action = action;
        this.isPlayerOne = isPlayerOne;
        this.untriedActions = (~(state | (state >>> OFFSET))) & BOARD_MASK; // state 0x0 becomes state 0x1FF
    }

    boolean isTerminal() {
        return checkWinner(state) != 0
                || ((state | (state >>> OFFSET)) & BOARD_MASK) == BOARD_MASK;
    }

    boolean isFullyExpanded() {
        return untriedActions == 0;
    }

    public MCTSNode expand() {
        int action = Integer.numberOfTrailingZeros(untriedActions);

        // Remove action
        untriedActions &= untriedActions - 1;

        int playerToMove = getCurrentPlayer(state);

        int newState = makeMove(state, playerToMove == 1, action);
        MCTSNode child = new MCTSNode(newState, this, action, playerToMove == 1);
        this.children[this.childCount++] = child;
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
        int nState = this.state;
        int player = getCurrentPlayer(nState);
        while (true) {
            int winner = checkWinner(nState);
            if (winner != 0) {
                return winner;
            }
            int occupied = (nState | (nState >>> OFFSET)) & BOARD_MASK; // merge both players moves
            int empty = (~occupied) & BOARD_MASK; // get empty spots as bit 1
            if (empty == 0) { // if no bit 1, then board is filled
                return 0;
            }
            // Count available moves
            int moves = empty;
            int amountAvailableMoves = rand.nextInt(Integer.bitCount(empty)); //
            int move;

            do {
                move = Integer.numberOfTrailingZeros(moves);
                moves &= moves - 1; // Store available move square 0-indexed

            } while (amountAvailableMoves-- > 0);
            nState = makeMove(nState, player == 1, move);
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

    public static int mctsSearch(int rootState, int iterations) {
        MCTSNode root = new MCTSNode(rootState, null, -1, false);
        for (int i = 0; i < iterations; i++) {
            MCTSNode node = root;
            while (!node.isTerminal() && node.isFullyExpanded()) {
                node = node.bestChild(2 * 1.4); // Constant scaled to range (-1,1)
            }
            if (!node.isTerminal() && !node.isFullyExpanded()) {
                node = node.expand();
            }
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

public class BitmapMCTSTTT {
    static int board = 0b000_000_000_000_000_000;
    static boolean isPlayerOne = true;
    static final int OFFSET = 9;
    static final int BOARD_MASK = 0x1FF;
    static int move = 0; // 0-8 + offset
    static final int[] WIN_MASKS = {
            0b000000111, // 0 1 2
            0b000111000, // 3 4 5
            0b111000000, // 6 7 8

            0b001001001, // 0 3 6
            0b010010010, // 1 4 7
            0b100100100, // 2 5 8

            0b100010001, // 0 4 8
            0b001010100 // 2 4 6
    };
    static int[] winCounter = new int[2];
    static final Random rand = new Random();
    static final int GAMES = 10000;

    public static void main(String[] args) {
        for (int rounds = 0; rounds < GAMES; rounds++) {
            if (rounds % 1000 == 0) {
                System.out.println("P1 wins: " + winCounter[0]);
                System.out.println("P2 wins: " + winCounter[1]);
                System.out.println("Draws: " + (rounds - (winCounter[0] + winCounter[1])));
            }
            for (int turn = 0; turn < 9; turn++) {
                // for (int[] row : board) {
                // System.out.printf("%d %d %d\n", row[0], row[1], row[2]);
                // }
                // System.out.println();

                if (isPlayerOne) {
                    move = MCTSNode.mctsSearch(board, 5000);
                    // List<Integer> empty = availableActions(board);
                    // move = empty.get(rand.nextInt(empty.size()));
                    // System.out.printf("MCTS move: %d,%d\n", move[0], move[1]);
                } else {
                    move = MCTSNode.mctsSearch(board, 5000);
                    // List<Integer> empty = availableActions(board);
                    // move = empty.get(rand.nextInt(empty.size()));
                    // // System.out.printf("Random move: %d,%d\n", move[0], move[1]);
                }

                board = makeMove(board, isPlayerOne, move);

                int winner = checkWinner(board);
                if (winner != 0) {
                    // for (int[] row : board) {
                    // System.out.printf("%d %d %d\n", row[0], row[1], row[2]);
                    // }
                    // System.out.println();
                    // System.out.printf("Winner %d\n", winner);
                    winCounter[isPlayerOne ? 0 : 1]++;
                    break;
                }
                isPlayerOne = !isPlayerOne;
            }
            // System.out.println("DRAW");
            board = 0;
            isPlayerOne = true;
            move = -1;
        }
        System.out.println("P1 wins: " + winCounter[0]);
        System.out.println("P2 wins: " + winCounter[1]);
        System.out.println("Draws: " + (GAMES - (winCounter[0] + winCounter[1])));
    }

    static int checkWinner(int state) {
        int playerBoard1, playerBoard2;

        playerBoard1 = state & BOARD_MASK;
        playerBoard2 = (state >>> OFFSET) & BOARD_MASK;

        for (int mask : WIN_MASKS) {
            if ((playerBoard1 & mask) == mask) {
                return 1;
            } else if ((playerBoard2 & mask) == mask) {
                return 2;
            }
        }

        return 0;
    }

    static List<Integer> availableActions(int state) {

        List<Integer> availableActs = new ArrayList<>();

        int occupied = (state | (state >>> 9)) & 0x1FF;
        int empty = (~occupied) & 0x1FF;

        while (empty != 0) {
            int position = Integer.numberOfTrailingZeros(empty);
            availableActs.add(position);
            // Remove this available position
            empty &= empty - 1;
        }

        return availableActs;
    }

    static int getCurrentPlayer(int state) {
        return (Integer.bitCount((state | (state >>> OFFSET)) & BOARD_MASK) & 1) == 0
                ? 1
                : 2;
    }

    static int makeMove(int state, boolean whichPlayer, int move) {
        return state | 1 << (whichPlayer ? move : move + OFFSET);
    }
}
