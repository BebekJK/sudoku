package sudoku;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
// import java.util.Arrays;

/**
 * The main Sudoku program
 */

public class SudokuMain extends JFrame {
   private static final long serialVersionUID = 1L; // to prevent serial warning

   // private variables
   // panel
   GameBoardPanel board = new GameBoardPanel();
   JPanel sidebar, header, statusBar;
   RightPane toolsPanel;
   MainBackground backcp;
   StartingPanel start = new StartingPanel();
   TopBottomPane topPane = new TopBottomPane(), bottomPane = new TopBottomPane();
   static BoxesLeft cellBoxesPanel;

   // label
   static JLabel lblTime = new JLabel("00:00"), lblHintLeft = new JLabel("Hint Left: " + 3),
         lblCellsLeft, lblMistakes = new JLabel("Mistakes: " + 0);

   // variables
   static int hintCount = 3, cellsLeft, mistakesCount = 0, seconds = 0;
   static int[] boxesCount = new int[9];

   // menubar
   MenuBar menubar = new MenuBar();

   // image, audio, timer
   AudioInputStream soundStream;
   private Clip bgMusic = null;
   private String imgPath;
   public static Timer timer;

   // Constructor
   public SudokuMain() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
      Container cp = getContentPane();
      cp.setLayout(new BorderLayout());
      cp.add(start, BorderLayout.CENTER);

      // ----- Starting Panel Configuration and Layout -----
      setStartDisabled();
      setDifficultyDisabled();
      start.getUserName().addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (!start.getUserName().getText().equals("")) {
               setDifficultyEnabled();
            } else {
               setDifficultyDisabled();
            }
         }
      });
      start.getStartButton().addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            cp.remove(start);
            revalidate();
            repaint();
            StartGame();
         }
      });

      start.getDifficultyButton("easy").addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            setStartEnabled();
            GameBoardPanel.difficulty = GameDifficulty.EASY;
         }
      });
      start.getDifficultyButton("medium").addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            setStartEnabled();
            GameBoardPanel.difficulty = GameDifficulty.MEDIUM;
         }
      });
      start.getDifficultyButton("hard").addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            setStartEnabled();
            GameBoardPanel.difficulty = GameDifficulty.HARD;
         }
      });
      start.getDifficultyButton("insane").addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            setStartEnabled();
            GameBoardPanel.difficulty = GameDifficulty.INSANE;
         }
      });

      // ----- end of --- Starting Panel Configuration -----




      // ----- MAIN Sudoku Layout and Configuration -----
      // HEADER (sub of main)
      header = new JPanel();
      header.setLayout(new GridLayout(1, 2));

      // TIMER (sub of header (sub of main))
      lblTime.setFont(new Font("Serif", Font.PLAIN, 80));
      lblTime.setHorizontalAlignment(JLabel.CENTER);
      lblTime.setVerticalAlignment(JLabel.CENTER);
      timer = new Timer(1000, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            seconds++;
            int lblMinute = seconds / 60;
            int lblSecond = seconds % 60;
            lblTime.setText(String.format("%02d:%02d", lblMinute, lblSecond));
         }
      });
      header.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
      header.add(lblTime);
      // ---------- END OF TIMER (sub of header (sub of main)) ----------
      header.setOpaque(false);
      // ---------- END OF HEADER (sub of main) ----------


      // STATUSBAR (sub of main)
      statusBar = new JPanel();
      
      // MISTAKES (sub of statusbar (sub of main)) 
      lblMistakes.setOpaque(false);
      lblMistakes.add(new JTransparentLabel("Mistakes: " + mistakesCount));
      lblMistakes.setBorder(BorderFactory.createEmptyBorder(8, 50, 8, 50));
      statusBar.add(lblMistakes);
      // ---------- END OF MISTAKES (sub of statusbar (sub of main)) ----------

      // CELLSLEFT (sub of statusbar (sub of main))
      lblCellsLeft = new JLabel("Cells left: " + cellsLeft);
      lblCellsLeft.setBorder(BorderFactory.createEmptyBorder(8, 50, 8, 50));
      statusBar.add(lblCellsLeft);
      // ---------- END OF CELLSLEFT (sub of statusbar (sub of main)) ----------
      
      
      // HINTS (sub of statusbar (sub of main))
      lblHintLeft.setBorder(BorderFactory.createEmptyBorder(8, 50, 8, 50));
      statusBar.add(lblHintLeft);
      // ---------- END OF HINTS (sub of statusbar (sub of main)) ----------
      statusBar.setOpaque(false);
      // ---------- END OF STATUSBAR (sub of main)
      // ---------- END OF MAIN ----------



      // Background Music (when game started)
      soundStream = AudioSystem.getAudioInputStream(new File("sudoku/bg_music_sudoku.wav"));
      bgMusic = AudioSystem.getClip();
      bgMusic.open(soundStream);


      // MENUBAR
      menubar.newGameItem.addActionListener(new restartGame());
      menubar.restartGameItem.addActionListener(new resetGame());
      menubar.getHintItem.addActionListener(new hintListener());
      menubar.toggleThemeItem.addActionListener(new toggleTheme());      
      // menubar.exitGameItem.addActionListener(new exitGame());
      menubar.toggleSoundItem.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt){
            if(menubar.toggleSoundItem.getText().equals("Disable Sound")){
               bgMusic.stop();
               menubar.toggleSoundItem.setText("Enable Sound");
            }
            else{
               bgMusic.start();
               menubar.toggleSoundItem.setText("Disable Sound");
            }
            
         }
      });

      setJMenuBar(menubar.menubar);
      // ---------- END OF MENUBAR ----------


      // Frame Template
      pack(); // Pack the UI components, instead of using setSize()
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // to handle window-closing
      setTitle("Sudoku");
      setSize(GameBoardPanel.BOARD_WIDTH, GameBoardPanel.BOARD_HEIGHT);
      setVisible(true);
   }

   /** The entry main() entry method */
   public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            try {
               new SudokuMain();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
               e.printStackTrace();
            }
         }
      });
   }

   private void StartGame() {
      Container cp = getContentPane();

      // Background Panel of Main Sudoku
      imgPath = GameBoardPanel.isDarkMode? "sudoku/bgdark.jpeg" : "sudoku/bglight.png";
      backcp = new MainBackground(imgPath);
      backcp.setLayout(new BorderLayout());

      // TOP PANE (consisting of header)
      topPane.add(header);
      topPane.add(new EmptyPanel());
      topPane.setOpaque(false);
      backcp.add(topPane, BorderLayout.NORTH);
      // ----- END OF TOP PANE -----

      // BOTTOM PANE (consisting of statusbar)
      bottomPane.add(statusBar);
      bottomPane.add(new EmptyPanel());
      bottomPane.setOpaque(false);
      backcp.add(bottomPane, BorderLayout.SOUTH);
      // ----- END OF BOTTOM PANE -----
      

      backcp.add(new EmptyPanel(), BorderLayout.WEST);


      // CENTER PANEL (consisting of sudokupanel and toolspanel)
      JPanel centerPanel = new JPanel();
      centerPanel.setLayout(new GridLayout(1, 2));
      backcp.add(centerPanel, BorderLayout.CENTER);

      // SUDOKU PANEL (sub of centerpanel)
      JPanel sudokuPane = new JPanel(new BorderLayout());
      sudokuPane.setOpaque(false);
      sudokuPane.setLayout(new BorderLayout());
      sudokuPane.add(board, BorderLayout.CENTER);
      sudokuPane.add(board);
      sudokuPane.setOpaque(false);
      centerPanel.add(sudokuPane);
      // ---------- END OF SUDOKU PANEL (sub of centerpanel)

      // TOOLS PANEL (sub of centerpanel)
      toolsPanel = new RightPane();
      cellBoxesPanel = new BoxesLeft();
      cellBoxesPanel.setOpaque(false);
      sidebar = toolsPanel.getSideBar();
      sidebar.add(new EmptyPanel());
      sidebar.add(cellBoxesPanel);
      sidebar.add(new EmptyPanel());
      sidebar.setOpaque(false);
      centerPanel.add(toolsPanel);
      toolsPanel.setOpaque(false);
      // ---------- END OF TOOLS PANEL (sub of centerpanel)
      centerPanel.setOpaque(false);
      // ---------- END OF CENTER PANEL ----------
      
      // Add everything to Content Pane
      cp.add(backcp);


      // Start game
      board.newGame();
      board.setOpaque(false);

      // Initialize CELLSLEFT
      cellsLeft = board.getCellsLeft();
      lblCellsLeft.setText("Cells left: " + cellsLeft);

      //Begin Timer and Music Background
      timer.start();
      bgMusic.setFramePosition(0);
      bgMusic.start();
      bgMusic.loop(Clip.LOOP_CONTINUOUSLY);

      // Initialize Box Count and Listener
      initializeBoxCount();
      initializeBoxFunction();

      // Recolor Font
      recolorFont();

      // Pack the frame
      pack();
   }

   private void setStartEnabled() {
      start.getStartButton().setEnabled(true);
   }

   private void setDifficultyEnabled() {
      start.getDifficultyButton("easy").setEnabled(true);
      start.getDifficultyButton("medium").setEnabled(true);
      start.getDifficultyButton("hard").setEnabled(true);
      start.getDifficultyButton("insane").setEnabled(true);
   }

   private void setStartDisabled() {
      start.getStartButton().setEnabled(false);
   }

   private void setDifficultyDisabled() {
      start.getDifficultyButton("easy").setEnabled(false);
      start.getDifficultyButton("medium").setEnabled(false);
      start.getDifficultyButton("hard").setEnabled(false);
      start.getDifficultyButton("insane").setEnabled(false);
   }

   private void initializeBoxCount() {
      for (int r=0; r<9; ++r) {
         for (int c=0; c<9; ++c) {
            Cell referenceCell = board.getCell(r, c);
            int cellNumber = referenceCell.number-1;
            if (referenceCell.status == CellStatus.GIVEN) {
               boxesCount[cellNumber] += 1;
            }
         }
      }
   }

   public void initializeBoxFunction(){
      JButton[] cellBoxesArray = cellBoxesPanel.getCellBoxes();
      for(int i=0; i<9; i++){
         JButton referenceBox = cellBoxesArray[i];
         referenceBox.addFocusListener(new highlightNumber());
         referenceBox.setEnabled(true);
      }
   }

   public void recolorFont() {
      lblCellsLeft.setForeground(GameBoardPanel.isDarkMode? Cell.FG_GIVEN_DARK: Cell.FG_GIVEN_LIGHT);
      lblHintLeft.setForeground(GameBoardPanel.isDarkMode? Cell.FG_GIVEN_DARK: Cell.FG_GIVEN_LIGHT);
      lblMistakes.setForeground(GameBoardPanel.isDarkMode? Cell.FG_GIVEN_DARK: Cell.FG_GIVEN_LIGHT);
      lblCellsLeft.setForeground(GameBoardPanel.isDarkMode? Cell.FG_GIVEN_DARK: Cell.FG_GIVEN_LIGHT);
      lblTime.setForeground(GameBoardPanel.isDarkMode? Cell.FG_GIVEN_DARK: Cell.FG_GIVEN_LIGHT);

      JButton[] cellBoxesArray = cellBoxesPanel.getCellBoxes();
      for (int i=0; i<9; ++i) {
         JButton referenceBox = cellBoxesArray[i];
         referenceBox.setForeground(GameBoardPanel.isDarkMode? Cell.FG_GIVEN_DARK: Cell.FG_GIVEN_LIGHT);
         referenceBox.setBackground(GameBoardPanel.isDarkMode? Cell.BG_GIVEN_ODD_DARK: Cell.FG_NOT_GIVEN_LIGHT);
      }
   }

   private class resetGame implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         timer.stop();
         timer.start();
         mistakesCount = 0;
         lblMistakes.setText("Mistakes: "+ mistakesCount);
         lblTime.setText("00:00");
         hintCount = 3;
         lblHintLeft.setText("Hints Left: " + hintCount);

         for (int row = 0; row < GameBoardPanel.GRID_SIZE; ++row) {
            for (int col = 0; col < GameBoardPanel.GRID_SIZE; ++col) {
               Cell referenceCell = board.getCell(row, col);
               if (referenceCell.status != CellStatus.GIVEN) {
                  referenceCell.status = CellStatus.TO_GUESS;
                  seconds = 0;
                  referenceCell.paint();
               }
               referenceCell.disabled = false;
            }
         }
         cellsLeft = board.getCellsLeft();
         lblCellsLeft.setText("Cells Left: " + cellsLeft);
         for (int row = 0; row < GameBoardPanel.GRID_SIZE; ++row) {
            for (int col = 0; col < GameBoardPanel.GRID_SIZE; ++col) {
               Cell referenceCell = board.getCell(row, col);
               if (referenceCell.isEditable()) {
                  referenceCell.requestFocus();
                  return;
               }
            }
         }

         initializeBoxCount();
         initializeBoxFunction();
         recolorFont();
      }
   }

   private class restartGame implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         for(int row=0;row<9;row++){
            for(int col=0;col<9;col++){
               board.getCell(row, col).disabled = false;
               repaint();
            }
         }
         mistakesCount = 0;
         lblMistakes.setText("Mistakes: "+ mistakesCount);
         lblTime.setText("00:00");
         cellsLeft = board.getCellsLeft();
         lblCellsLeft.setText("Cells Left: " + cellsLeft);
         hintCount = 3;
         lblHintLeft.setText("Hints Left: " + hintCount);
         timer.stop();
         timer.start();
         seconds = 0;
         board.newGame();
         initializeBoxCount();
         initializeBoxFunction();
         recolorFont();

         revalidate();
         repaint();
      }
   }

   private class hintListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         int randomRow = (int) (Math.random() * 9);
         int randomCol = (int) (Math.random() * 9);
         Cell referenceCell = board.getCell(randomRow, randomCol);

         while (referenceCell.status != CellStatus.TO_GUESS && hintCount > 0) {
            randomRow = (int) (Math.random() * 9);
            randomCol = (int) (Math.random() * 9);
            referenceCell = board.getCell(randomRow, randomCol);
         }
         System.out.println(randomRow + "," + randomCol);

         hintCount--;
         if (referenceCell.status == CellStatus.TO_GUESS && hintCount >= 0) {
            cellsLeft -= 1;
            lblCellsLeft.setText("Cells Left: " + cellsLeft);
            lblHintLeft.setText("Hints Left: " + hintCount);
            referenceCell.setText("" + referenceCell.number);
            referenceCell.status = CellStatus.CORRECT_GUESS;
            referenceCell.paint();
         }
      }
   }

   private class toggleTheme implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent evt) {
         GameBoardPanel.isDarkMode = !GameBoardPanel.isDarkMode;
         for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
               board.getCell(row, col).paint();
            }
         }
         recolorFont();
         imgPath = GameBoardPanel.isDarkMode? "sudoku/bgdark.jpeg" : "sudoku/bglight.png";
         backcp.setBackgroundImage(imgPath);
         backcp.revalidate();
         backcp.repaint();
      }
   }

   private class highlightNumber implements FocusListener {
      @Override
      public void focusGained(FocusEvent evt) {
         JButton pressedButton = (JButton) evt.getSource();
         int pressedNumber = Integer.parseInt(pressedButton.getText());

         pressedButton.setOpaque(true);
         // pressedButton.setHighlight(GameBoardPanel.isDarkMode? Cell.BG_GIVEN_ODD_DARK: Cell.FG_NOT_GIVEN_LIGHT);

         System.out.println(pressedButton.getBackground());
         for(int row=0; row<9; row++){
            for(int col=0; col<9; col++){
               Cell currCell = board.getCell(row, col);
               if(currCell.number == pressedNumber && (currCell.status == CellStatus.GIVEN || currCell.status == CellStatus.CORRECT_GUESS)){
                  currCell.mainFocus = true;
                  currCell.focus = true;
                  currCell.paint();
               }
            }
         }
      }

      @Override
      public void focusLost(FocusEvent evt) {
         JButton pressedButton = (JButton) evt.getSource();
         int pressedNumber = Integer.parseInt(pressedButton.getText());

         for(int row=0; row<9; row++){
            for(int col=0; col<9; col++){
               Cell currCell = board.getCell(row, col);
               if(currCell.number == pressedNumber){
                  currCell.mainFocus = false;
                  currCell.focus = false;
                  currCell.paint();
               }
            }
         }
      }
   }

   private class exitGame implements ActionListener{
      @Override
      public void actionPerformed(ActionEvent evt){
         bgMusic.stop();
         SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               try {
                  dispose();
                  new SudokuMain();
               } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                  e.printStackTrace();
               }
            }
         });
      }
   }
}