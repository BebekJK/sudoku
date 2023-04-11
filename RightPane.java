package sudoku;

import java.awt.*;
import javax.swing.*;

public class RightPane extends JPanel {
    private JPanel leftHalfPage;
    public RightPane() {
        // divide page by half
        setLayout(new GridLayout(1, 2));
        // divide the left half page by 3 columns
        leftHalfPage = new JPanel();
        leftHalfPage.setLayout(new GridLayout(1, 3));
        add(leftHalfPage);
        add(new EmptyPanel());
    }

    public JPanel getSideBar() {
        return leftHalfPage;
    }
}