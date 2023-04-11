import java.awt.*;
import javax.swing.*;

public class BoxesLeft extends JPanel {
    private JTextField[] cellBoxes;

    public BoxesLeft() {
        setLayout(new GridLayout(10, 1));
        cellBoxes = new JTextField[9];

        for (int i=0; i<9; ++i) {
            cellBoxes[i] = new JTextField();
            cellBoxes[i].setText("" + (i+1));
            cellBoxes[i].setEditable(false);
            cellBoxes[i].setHorizontalAlignment(JTextField.CENTER);
            add(cellBoxes[i]);
        }
    }

    public JTextField[] getCellBoxes() {
        return cellBoxes;
    }
}

