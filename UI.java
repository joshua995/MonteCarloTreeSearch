import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class UI {
    static final int WIDTH = 500;
    static final int HEIGHT = 500;

    static JButton[] buttons = new JButton[9];
    static int buttonI = 0;

    static int chosenMove = -1;

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(WIDTH, HEIGHT);
        frame.setLayout(null);
        frame.setUndecorated(true);
        frame.setVisible(true);

        createButtons(frame, 0, 0, WIDTH / 3, WIDTH / 3);
        createButtons(frame, WIDTH / 3, 0, WIDTH / 3, WIDTH / 3);
        createButtons(frame, WIDTH / 3 * 2, 0, WIDTH / 3, WIDTH / 3);

        createButtons(frame, 0, HEIGHT / 3, WIDTH / 3, WIDTH / 3);
        createButtons(frame, WIDTH / 3, HEIGHT / 3, WIDTH / 3, WIDTH / 3);
        createButtons(frame, WIDTH / 3 * 2, HEIGHT / 3, WIDTH / 3, WIDTH / 3);

        createButtons(frame, 0, HEIGHT / 3 * 2, WIDTH / 3, WIDTH / 3);
        createButtons(frame, WIDTH / 3, HEIGHT / 3 * 2, WIDTH / 3, WIDTH / 3);
        createButtons(frame, WIDTH / 3 * 2, HEIGHT / 3 * 2, WIDTH / 3, WIDTH / 3);

        while (true) {
            System.out.println(chosenMove);
        }

    }

    static void createButtons(JFrame frame, int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setBounds(x, y, width, height);
        frame.add(button);
        System.out.println(buttonI);
        int temp = buttonI;
        buttons[buttonI++] = button;
        button.addActionListener(e -> chosenMove = temp);
    }

    static void removeButtonListener(int buttonI) {
        buttons[buttonI].removeActionListener(buttons[buttonI].getActionListeners()[0]);
    }

    static void makeMove() {

    }
}
