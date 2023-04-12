package sudoku;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class PlaceholderTextField extends JTextField implements FocusListener {

    private String placeholder;
    private boolean isPlaceholderSet;

    public PlaceholderTextField() {
        super();
        addFocusListener(this);
    }

    public PlaceholderTextField(final String pText) {
        super(pText);
        addFocusListener(this);
        placeholder = pText;
        isPlaceholderSet = true;
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (isPlaceholderSet) {
            isPlaceholderSet = false;
            setText("");
            setForeground(Color.BLACK);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (getText().isEmpty()) {
            setText(placeholder);
            isPlaceholderSet = true;
            setForeground(Color.GRAY);
        }
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(final String s) {
        placeholder = s;
        setText(s);
        isPlaceholderSet = true;
        setForeground(Color.GRAY);
    }

    @Override
    protected void paintComponent(Graphics pG) {
        super.paintComponent(pG);

        if (isPlaceholderSet && !getText().isEmpty()) {
            pG.setColor(Color.GRAY);
            pG.drawString(placeholder, getInsets().left, pG.getFontMetrics()
                    .getMaxAscent() + getInsets().top);
        }
    }
}
