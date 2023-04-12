

import java.awt.*;
import javax.swing.*;

public class BoxesLeft extends JPanel {
    private JButton[] cellBoxes;

    public BoxesLeft() {
        setLayout(new GridLayout(11, 1));
        cellBoxes = new JButton[9];

        add(new EmptyPanel());
        for (int i = 0; i < 9; ++i) {
            cellBoxes[i] = new JButton();
            cellBoxes[i].setPreferredSize(new Dimension(GameBoardPanel.GRID_SIZE, GameBoardPanel.GRID_SIZE));
            cellBoxes[i].setText("" + (i + 1));
            cellBoxes[i].setHorizontalAlignment(JTextField.CENTER);
            cellBoxes[i].setFont(Cell.FONT_NUMBERS);
            add(cellBoxes[i]);
        }
    }

    public JButton[] getCellBoxes() {
        return cellBoxes;
    }
}
