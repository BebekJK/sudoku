import java.awt.*;
import javax.swing.*;

public class RightPane extends JPanel {
    private JPanel leftHalfPage;

    public RightPane() {
        setLayout(new GridLayout(1, 2));

        leftHalfPage = new JPanel();
        leftHalfPage.setLayout(new GridLayout(1, 3));
        add(leftHalfPage);
        add(new EmptyPanel());
    }

    public JPanel getSideBar() {
        return leftHalfPage;
    }
}