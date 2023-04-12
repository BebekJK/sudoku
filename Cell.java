package sudoku;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
// import javax.swing.border.CompoundBorder;
// import javax.swing.border.EmptyBorder;

/**
 * The Cell class model the cells of the Sudoku puzzle, by customizing (subclass)
 * the javax.swing.JTextField to include row/column, puzzle number and status.
 */
public class Cell extends JTextField {
   private static final long serialVersionUID = 1L;  // to prevent serial warning
   
   public static final Color LightBeige = new Color(230, 211, 173);
   public static final Color PaleYellow = new Color(242, 232, 196);
   public static final Color LightBlue = new Color(130, 207, 253);
   public static final Color White = new Color(255,255,255);
   public static final Color LightRed = new Color(255,117,117);

   public static final Color DarkGray = new Color(121,121,121);
   public static final Color DarkerGray= new Color(64,64,64);
   public static final Color BrighterBlue = new Color(76,150,243);
   public static final Color VeryDarkGray = new Color(29,29,29);

   

   public static final Color BG_GIVEN_EVEN_LIGHT = LightBeige;
   public static final Color BG_GIVEN_ODD_LIGHT =  PaleYellow;
   public static final Color FG_GIVEN_LIGHT = Color.BLACK;
   public static final Color FG_NOT_GIVEN_LIGHT = Color.BLACK;
   public static final Color BG_TO_GUESS_LIGHT =  White;
   public static final Color BG_CORRECT_GUESS_LIGHT = Color.GREEN;
   public static final Color BG_WRONG_GUESS_LIGHT  = LightRed;

   public static final Color BG_GIVEN_EVEN_DARK = DarkGray;
   public static final Color BG_GIVEN_ODD_DARK = DarkerGray;
   public static final Color FG_GIVEN_DARK = Color.WHITE;
   public static final Color FG_NOT_GIVEN_DARK = Color.WHITE;
   public static final Color BG_TO_GUESS_DARK  = VeryDarkGray;
   public static final Color BG_CORRECT_GUESS_DARK = Color.GREEN;
   public static final Color BG_WRONG_GUESS_DARK  = LightRed;

   public static final Font FONT_NUMBERS = new Font("OCR A Extended", Font.PLAIN, 28);
   // Define properties (package-visible)
   /** The row and column number [0-8] of this cell */
   int row, col;
   /** The puzzle number [1-9] for this cell */
   int number;
   boolean focus, mainFocus, disabled;
   /** The status of this cell defined in enum CellStatus */
   CellStatus status;

   /** Constructor */
   public Cell(int row, int col) {
      super();   // JTextField
      this.row = row;
      this.col = col;
      focus = false;
      // Inherited from JTextField: Beautify all the cells once for all
      super.setHorizontalAlignment(JTextField.CENTER);
      super.setFont(FONT_NUMBERS);
   }

   /** Reset this cell for a new game, given the puzzle number and isGiven */
   public void newGame(int number, boolean isGiven) {
      this.number = number;
      status = isGiven ? CellStatus.GIVEN : CellStatus.TO_GUESS;
      focus = false;
      paint();    // paint itself
   }

   /** This Cell (JTextField) paints itself based on its status */
   public void paint() {
      if(disabled){
         setEditable(false);
         return;
      }
      if (status == CellStatus.GIVEN) {
         // Inherited from JTextField: Set display properties
         super.setText(number + "");
         super.setEditable(false);
         if((row/3 + col/3)%2 == 0) super.setBackground(GameBoardPanel.isDarkMode? BG_GIVEN_EVEN_DARK : BG_GIVEN_EVEN_LIGHT);
         else super.setBackground(GameBoardPanel.isDarkMode? BG_GIVEN_ODD_DARK : BG_GIVEN_ODD_LIGHT);
         super.setForeground(GameBoardPanel.isDarkMode? FG_GIVEN_DARK : FG_GIVEN_LIGHT);
      } else if (status == CellStatus.TO_GUESS) {
         // Inherited from JTextField: Set display properties
         super.setText("");
         super.setEditable(true);
         super.setBackground(GameBoardPanel.isDarkMode? BG_TO_GUESS_DARK : BG_TO_GUESS_LIGHT);
         super.setForeground(GameBoardPanel.isDarkMode? FG_NOT_GIVEN_DARK : FG_NOT_GIVEN_LIGHT);
      } else if (status == CellStatus.CORRECT_GUESS) { 
          // from TO_GUESS
         if((row/3 + col/3)%2 == 0) super.setBackground(GameBoardPanel.isDarkMode? BG_GIVEN_EVEN_DARK : BG_GIVEN_EVEN_LIGHT);
         else super.setBackground(GameBoardPanel.isDarkMode? BG_GIVEN_ODD_DARK : BG_GIVEN_ODD_LIGHT);
         setForeground(GameBoardPanel.isDarkMode? FG_GIVEN_DARK : FG_GIVEN_LIGHT);
         // super.setBackground(BG_CORRECT_GUESS);
      } else if (status == CellStatus.WRONG_GUESS) {    // from TO_GUESS
         super.setBackground(GameBoardPanel.isDarkMode? BG_WRONG_GUESS_DARK : BG_WRONG_GUESS_LIGHT);
      }


      ;
      if(focus){
         if(mainFocus) setBorder(BorderFactory.createLineBorder(GameBoardPanel.isDarkMode? LightBlue: BrighterBlue, 5));
         if(isEditable() && status!=CellStatus.CORRECT_GUESS && status!=CellStatus.WRONG_GUESS) setBackground(GameBoardPanel.isDarkMode? BrighterBlue: LightBlue);
      }
      else{
         setBorder(BorderFactory.createLineBorder(GameBoardPanel.isDarkMode? FG_GIVEN_DARK : FG_GIVEN_LIGHT, 1));
         // setBorder(null);
      }

   }
}