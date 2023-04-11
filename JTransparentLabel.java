

import java.awt.*;
import javax.swing.*;

public class JTransparentLabel extends JLabel{
    public JTransparentLabel(){
        setOpaque(false);
        setText("");
    }

    public JTransparentLabel(String text){
        setOpaque(false);
        setText(text);
    }

    public JTransparentLabel(String text, Color color){
        setOpaque(false);
        setText(text);
        setForeground(color);
    }

    // public void setFont(Font font){
    //     setFont(font);
    // }
}
