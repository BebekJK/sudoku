

import java.awt.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
public class MainBackground extends JPanel{
    private BufferedImage backgroundImage;

    public MainBackground(String imagePath){
        setLayout(new BorderLayout());
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    
        // Set the panel to fill the entire frame
        // setPreferredSize(new Dimension(GameBoardPanel.BOARD_WIDTH, GameBoardPanel.BOARD_HEIGHT));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
    }

    public void setBackgroundImage(String imagePath){
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    // public static void main(String[] args) {
    //     JFrame frame = new JFrame("Background Image Panel");
    //     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //     frame.setSize(400, 400);
    //     frame.setContentPane(new MainBackground());
    //     frame.setVisible(true);
    // }
}
