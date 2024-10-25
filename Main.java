import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

class MCTS {
    Node root;

    Node search(Board initialState, Node parent) {
        this.root = new Node(initialState, parent);
        for (int i = 0; i < 1000; i++) {
            Node node = this.select(this.root);
            int score = this.rollout(node.board);
            this.backpropagate(node, score);
        }
        return this.getBestMove(this.root, 0);
    }

    private void backpropagate(Node node, int score) {
        while (node != null) {
            node.visits += 1;
            node.score += score;
            node = node.parent;
        }
    }

    private int rollout(Board board) {
        while (!board.isWin()) {
            try {
                ArrayList<Board> tempBoards = board.generateStates();
                board = new Board(tempBoards.get(new Random().nextInt(0, tempBoards.size())));
            } catch (Exception e) {
                return 0;
            }
        }
        // Could be wrong
        if (board.nextPlayer == "x")
            return 1;
        else if (board.nextPlayer == "o")
            return -1;
        return -2;
    }

    private Node select(Node node) {
        while (!node.isTerminalNode) {
            if (node.isFullyExpanded)
                node = this.getBestMove(node, 2);
            else
                return this.expand(node);
        }
        return node;
    }

    private Node expand(Node node) {
        ArrayList<Board> states = node.board.generateStates();
        for (Board state : states) {
            if (!node.children.keySet().contains(state.positionToString())) {
                Node new_node = new Node(state, node);
                node.children.put(state.positionToString(), new_node);
                if (states.size() == node.children.size())
                    node.isFullyExpanded = true;
                return new_node;
            }
        }
        return null;
    }

    Node getBestMove(Node node, int explorationConstant) {
        double bestScore = Double.NEGATIVE_INFINITY;
        ArrayList<Node> bestMoves = new ArrayList<>();
        for (Node childNode : node.children.values()) {
            int currentPlayerInt = 0;
            if (childNode.board.nextPlayer == "x") {
                currentPlayerInt = 1;
            } else {
                currentPlayerInt = -1;
            }
            double moveScore = (currentPlayerInt * childNode.score / childNode.visits)
                    + (explorationConstant * Math.sqrt(
                            Math.log(node.visits / childNode.visits)));

            if (moveScore > bestScore) {
                bestScore = moveScore;
                bestMoves.clear();
                bestMoves.add(childNode);
            } else if (moveScore == bestScore)
                bestMoves.add(childNode);
        }
        Node move = null;
        int moveI = new Random().nextInt(0, bestMoves.size());
        try {
            move = bestMoves.get(moveI);
        } catch (Exception e) {

        }
        return move;
    }
}

class Node {
    Board board;
    boolean isTerminalNode;
    boolean isFullyExpanded;
    Node parent;
    int visits;
    int score;
    HashMap<String, Node> children = new HashMap<>();

    public Node(Board board, Node parent) {
        this.board = board;
        if (board.isWin() || board.isDraw())
            this.isTerminalNode = true;
        else
            this.isTerminalNode = false;
        this.isFullyExpanded = this.isTerminalNode;
        this.parent = parent;
        this.visits = 0;
        this.score = 0;
    }
}

class Board {
    String currentPlayer = "x";
    String nextPlayer = "o";
    String empty = ".";
    String[] position = new String[9];
    Board board = this;

    public Board(Board brd) {
        initBoard();
        if (brd != null) {
            this.board.currentPlayer = brd.currentPlayer;
            this.board.nextPlayer = brd.nextPlayer;
            this.board.position = deepCopy(brd.position);
        }

    }

    String[] deepCopy(String[] pos) {
        String[] copy = new String[9];
        for (int i = 0; i < 9; i++) {
            copy[i] = pos[i];
        }
        return copy;
    }

    void initBoard() {
        for (int i = 0; i < position.length; i++) {
            position[i] = empty;
        }
    }

    Board makeMove(int index) {
        Board _board = new Board(this.board);
        _board.position[index] = _board.currentPlayer;
        String temp = _board.currentPlayer;
        _board.currentPlayer = _board.nextPlayer;
        _board.nextPlayer = temp;
        return _board;
    }

    public boolean isDraw() {
        for (String s : board.position) {
            if (s.contains(board.empty))
                return false;
        }
        return true;
    }

    public boolean isWin() {
        if ((board.position[0] == board.position[1] && board.position[2] == board.position[1]
                && board.position[0] != board.empty)
                || (board.position[3] == board.position[4] && board.position[5] == board.position[4]
                        && board.position[5] != board.empty)
                ||
                (board.position[6] == board.position[7] && board.position[8] == board.position[7]
                        && board.position[8] != board.empty)
                ||
                (board.position[0] == board.position[3] && board.position[6] == board.position[3]
                        && board.position[6] != board.empty)
                ||
                (board.position[1] == board.position[4] && board.position[7] == board.position[4]
                        && board.position[7] != board.empty)
                ||
                (board.position[2] == board.position[5] && board.position[8] == board.position[5]
                        && board.position[8] != board.empty)
                ||
                (board.position[0] == board.position[4] && board.position[8] == board.position[4]
                        && board.position[8] != board.empty)
                ||
                (board.position[2] == board.position[4] && board.position[6] == board.position[4]
                        && board.position[6] != board.empty))
            return true;
        return false;
    }

    ArrayList<Board> generateStates() {
        ArrayList<Board> actions = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (board.position[i] == board.empty) {
                actions.add(board.makeMove(i));
                System.out.println(board.positionToString());
            }
        }
        return actions;
    }

    void gameLoop() {
        System.out.println("\n\n");
        displayBoard(board);

        MCTS mcts = new MCTS();

        Scanner scan = new Scanner(System.in);
        while (true) {
            String input = scan.nextLine();
            if (input.contains("exit"))
                break;
            else if (input == "")
                continue;
            try {
                Integer.parseInt(input);
            } catch (Exception e) {
                continue;
            }

            this.board = this.board.makeMove(Integer.parseInt(input));
            displayBoard(board);
            Node bestMove = mcts.search(this.board, null);
            try {
                this.board = new Board(bestMove.board);
            } catch (Exception e) {

            }

            displayBoard(board);
            if (this.board.isWin()) {
                System.out.printf("player %s has won the game!\n", board.nextPlayer);
                break;
            } else if (this.board.isDraw()) {
                System.out.println("Game is drawn!\n");
                break;
            }
        }
        scan.close();
    }

    void displayBoard(Board board) {
        System.out.printf("%s|%s|%s\n", board.position[0], board.position[1], board.position[2]);
        System.out.println("-+-+-");
        System.out.printf("%s|%s|%s\n", board.position[3], board.position[4], board.position[5]);
        System.out.println("-+-+-");
        System.out.printf("%s|%s|%s\n", board.position[6], board.position[7], board.position[8]);
    }

    String positionToString() {
        return position[0] + position[1] + position[2] + position[3] + position[4] + position[5] + position[6]
                + position[7] + position[8];
    }
}

public class Main {
    public static void main(String[] args) {
        Board board = new Board(null);
        board.gameLoop();
    }
}