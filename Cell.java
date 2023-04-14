import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class Cell extends JTextField {
   private static final long serialVersionUID = 1L; // to prevent serial warning

   public static final Color LightBeige = new Color(230, 211, 173);
   public static final Color PaleYellow = new Color(252, 242, 196);
   public static final Color LightBlue = new Color(130, 207, 253);
   public static final Color White = new Color(255, 255, 255);
   public static final Color LightRed = new Color(255, 117, 117);

   public static final Color DarkGray = new Color(121, 121, 121);
   public static final Color DarkerGray = new Color(64, 64, 64);
   public static final Color BrighterBlue = new Color(76, 150, 243);
   public static final Color VeryDarkGray = new Color(29, 29, 29);

   public static final Color BG_GIVEN_EVEN_LIGHT = LightBeige;
   public static final Color BG_GIVEN_ODD_LIGHT = PaleYellow;
   public static final Color FG_GIVEN_LIGHT = Color.BLACK;
   public static final Color FG_NOT_GIVEN_LIGHT = Color.BLACK;
   public static final Color BG_TO_GUESS_LIGHT = White;
   public static final Color BG_CORRECT_GUESS_LIGHT = Color.GREEN;
   public static final Color BG_WRONG_GUESS_LIGHT = LightRed;

   public static final Color BG_GIVEN_EVEN_DARK = DarkGray;
   public static final Color BG_GIVEN_ODD_DARK = DarkerGray;
   public static final Color FG_GIVEN_DARK = Color.WHITE;
   public static final Color FG_NOT_GIVEN_DARK = Color.WHITE;
   public static final Color BG_TO_GUESS_DARK = VeryDarkGray;
   public static final Color BG_CORRECT_GUESS_DARK = Color.GREEN;
   public static final Color BG_WRONG_GUESS_DARK = LightRed;

   public static final Font FONT_NUMBERS = new Font("OCR A Extended", Font.PLAIN, 28);
   int row, col;
   int number;
   boolean focus, mainFocus, disabled;
   CellStatus status;

   public Cell(int row, int col) {
      super(); 
      this.row = row;
      this.col = col;
      focus = false;

      super.setHorizontalAlignment(JTextField.CENTER);
      super.setFont(FONT_NUMBERS);
   }

   public void newGame(int number, boolean isGiven) {
      this.number = number;
      status = isGiven ? CellStatus.GIVEN : CellStatus.TO_GUESS;
      focus = false;
      paint(); 
   }

   public void paint() {
      if (disabled) {
         setEditable(false);
         return;
      }
      if (status == CellStatus.GIVEN) {
         super.setText(number + "");
         super.setEditable(false);
         if ((row / 3 + col / 3) % 2 == 0)
            super.setBackground(GameBoardPanel.isDarkMode ? BG_GIVEN_EVEN_DARK : BG_GIVEN_EVEN_LIGHT);
         else
            super.setBackground(GameBoardPanel.isDarkMode ? BG_GIVEN_ODD_DARK : BG_GIVEN_ODD_LIGHT);
         super.setForeground(GameBoardPanel.isDarkMode ? FG_GIVEN_DARK : FG_GIVEN_LIGHT);
      } else if (status == CellStatus.TO_GUESS) {
         super.setText("");
         super.setEditable(true);
         super.setBackground(GameBoardPanel.isDarkMode ? BG_TO_GUESS_DARK : BG_TO_GUESS_LIGHT);
         super.setForeground(GameBoardPanel.isDarkMode ? FG_NOT_GIVEN_DARK : FG_NOT_GIVEN_LIGHT);
      } else if (status == CellStatus.CORRECT_GUESS) {
         if ((row / 3 + col / 3) % 2 == 0)
            super.setBackground(GameBoardPanel.isDarkMode ? BG_GIVEN_EVEN_DARK : BG_GIVEN_EVEN_LIGHT);
         else
            super.setBackground(GameBoardPanel.isDarkMode ? BG_GIVEN_ODD_DARK : BG_GIVEN_ODD_LIGHT);
         setForeground(GameBoardPanel.isDarkMode ? FG_GIVEN_DARK : FG_GIVEN_LIGHT);
      } else if (status == CellStatus.WRONG_GUESS) { 
         super.setBackground(GameBoardPanel.isDarkMode ? BG_WRONG_GUESS_DARK : BG_WRONG_GUESS_LIGHT);
      }

      ;
      if (focus) {
         if (mainFocus)
            setBorder(BorderFactory.createLineBorder(GameBoardPanel.isDarkMode ? LightBlue : BrighterBlue, 5));
         if (isEditable() && status != CellStatus.CORRECT_GUESS && status != CellStatus.WRONG_GUESS)
            setBackground(GameBoardPanel.isDarkMode ? BrighterBlue : LightBlue);
      } else {
         setBorder(BorderFactory.createLineBorder(GameBoardPanel.isDarkMode ? FG_GIVEN_DARK : FG_GIVEN_LIGHT, 1));
      }
   }
}