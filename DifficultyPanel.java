package sudoku;

import java.awt.*;

import javax.swing.*;

public class DifficultyPanel extends JPanel {
    private JButton easy, medium, hard, insane;

    public DifficultyPanel() {
        setLayout(new GridLayout(5, 1));
        easy = new JButton("Easy");
        medium = new JButton("Medium");
        hard = new JButton("Hard");
        insane = new JButton("Insane");
        JLabel label = new JLabel("Choose Difficulty", SwingUtilities.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));

        add(label);
        add(easy);
        add(medium);
        add(hard);
        add(insane);
    }

    public JButton getButton(String type) {
        switch (type) {
            case "easy":
                return easy;
            case "medium":
                return medium;
            case "hard":
                return hard;
            case "insane":
                return insane;
            default:
                return new JButton();
        }
    }
}
