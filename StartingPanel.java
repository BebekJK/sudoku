import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class StartingPanel extends JPanel {
    private PlaceholderTextField usernameField;
    private JButton startButton;
    private JButton easy, medium, hard, insane;
    private BufferedImage backgroundImage;

    public StartingPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setPreferredSize(new Dimension(GameBoardPanel.BOARD_WIDTH, GameBoardPanel.BOARD_HEIGHT));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int newVerticalGap = (int) (getHeight() * 0.5);
                setLayout(new FlowLayout(FlowLayout.CENTER, 0, newVerticalGap));
                revalidate();
            }
        }); 

        System.out.println(getHeight());
        JPanel test = new JPanel();
        test.setLayout(new GridLayout(2, 1));

        JPanel startingComponentsPanel = new JPanel();
        startingComponentsPanel.setPreferredSize(new Dimension(400, 150));
        startingComponentsPanel.setOpaque(false);
        startingComponentsPanel.setLayout(new GridLayout(3, 1));
        startingComponentsPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int newWidth = (int) (getWidth() * 0.8);
                setSize(newWidth, getHeight());
                revalidate();
            }
        });

        // startingComponentsPanel.setSize();
        usernameField = new PlaceholderTextField();
        usernameField.setOpaque(false);
        usernameField.setPlaceholder("input your username...");

        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new GridLayout(1, 4));

        easy = new JButton("Easy");
        medium = new JButton("Medium");
        hard = new JButton("Hard");
        insane = new JButton("Insane");
        difficultyPanel.add(easy);
        difficultyPanel.add(medium);
        difficultyPanel.add(hard);
        difficultyPanel.add(insane);
        difficultyPanel.setOpaque(false);
        startButton = new JButton("Start Game");

        // startingComponentsPanel.add(new JLabel("Username", SwingConstants.CENTER),
        // BorderLayout.NORTH);

        startingComponentsPanel.add(usernameField);
        startingComponentsPanel.add(difficultyPanel);
        startingComponentsPanel.add(startButton);


        setBackgroundImage();
        add(startingComponentsPanel);
        // Set the panel to fill the entire frame

    }

    public JButton getStartButton() {
        return startButton;
    }

    public JTextField getUserName() {
        return usernameField;
    }

    public JButton getDifficultyButton(String type) {
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

    public void setBackgroundImage() {
        try {
            backgroundImage = ImageIO
                    .read(new File("startingBackground.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
    }

}
