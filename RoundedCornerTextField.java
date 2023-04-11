import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;

public class RoundedCornerTextField extends JTextField {
    private Shape shape;

    public RoundedCornerTextField(int size) {
        super(size);
        setOpaque(false); // make the text field transparent
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8)); // add padding to the text field
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20); // set the roundness of the corners here
        }
        g2.setClip(shape);
        super.paintComponent(g2);
        g2.dispose();
    }
}

