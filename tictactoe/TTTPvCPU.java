package tictactoe;
/*
Joshua Liu
Bitmap Tic Tac Toe with MCTS
2026-Sep-17
P vs CPU
*/

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class MCTSNode extends TTTPvCPU {
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
        this.untriedActions = (~(state | (state >>> OFFSET))) & BOARD_MASK;
    }

    boolean isTerminal() {
        return checkWinner(state) != 0
                || ((state | (state >>> OFFSET)) & BOARD_MASK) == BOARD_MASK; // Ensure there are legal moves available
    }

    boolean isFullyExpanded() {
        return untriedActions == 0;
    }

    public MCTSNode expand() {
        int action = Integer.numberOfTrailingZeros(untriedActions);

        // Remove action
        untriedActions &= untriedActions - 1;

        int playerToMove = getCurrentPlayer(state);

        int newState = state | (1 << (playerToMove == 1 ? action : action + OFFSET));
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
            int occupied = (nState | (nState >>> OFFSET)) & BOARD_MASK;
            int empty = (~occupied) & BOARD_MASK;
            if (empty == 0) {
                return 0;
            } // Count available moves
            int moves = empty;
            int target = rand.nextInt(Integer.bitCount(empty));
            int move;

            do {
                move = Integer.numberOfTrailingZeros(moves);
                moves &= moves - 1;
            } while (target-- > 0);
            nState |= 1 << (player == 1 ? move : move + OFFSET);
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
                node = node.bestChild(2 * 1.4);
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

public class TTTPvCPU {
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
    static final Random rand = new Random();

    static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        for (int turn = 0; turn < 9; turn++) {
            printBoard(board);

            if (isPlayerOne) {
                // move = MCTSNode.mctsSearch(board, 5000);
                System.out.println("Enter your move (1-9)");
                Integer input = scanner.nextInt();
                System.out.println(input);
                move = input - 1;
                while (!availableActions(board).contains(move)) {
                    System.out.println("Try again (1-9)");
                    input = scanner.nextInt();
                    move = input - 1;
                }

            } else {
                move = MCTSNode.mctsSearch(board, 5000);
            }

            board |= 1 << (isPlayerOne ? move : move + OFFSET); // Make a move

            int winner = checkWinner(board);
            if (winner != 0) {
                printBoard(board);
                System.out.printf("Winner %s\n", winner == 1 ? "X" : "O");
                break;
            }
            isPlayerOne = !isPlayerOne;
        }
        System.out.println("Game Over");
    }

    static void printBoard(int state) {
        int p1 = state & BOARD_MASK;
        int p2 = (state >>> OFFSET) & BOARD_MASK;

        for (int i = 0; i < 9; i++) {
            if ((p1 & (1 << i)) != 0)
                System.out.print("X");
            else if ((p2 & (1 << i)) != 0)
                System.out.print("O");
            else
                System.out.print(i + 1);

            if (i % 3 != 2)
                System.out.print("|");
            else if (i != 8)
                System.out.print("\n-+-+-\n");
        }

        System.out.println("\n");
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
}
