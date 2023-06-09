import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class SudokuMain extends JFrame {
   private static final long serialVersionUID = 1L; 
   GameBoardPanel board = new GameBoardPanel();
   JPanel sidebar, header, statusBar, centerPanel = new JPanel();
   RightPane toolsPanel;
   MainBackground backcp;
   StartingPanel start = new StartingPanel();
   TopBottomPane topPane = new TopBottomPane(), bottomPane = new TopBottomPane();
   static BoxesLeft cellBoxesPanel;
   JOptionPane gameOverPane = board.getGameOverPane();
   int disabledCount;
   static JLabel lblTime = new JLabel("00:00"), lblHintLeft = new JLabel("Hint Left: " + 3),
         lblCellsLeft, lblMistakes = new JLabel("Mistakes: " + 0);
   static int hintCount = 3, cellsLeft, mistakesCount = 0, seconds = 0;
   static int[] boxesCount = new int[9];
   Container cp = getContentPane();
   MenuBar menubar = new MenuBar();
   AudioInputStream soundStream;
   private Clip bgMusic = null;
   private String imgPath;
   public static Timer timer;

   public SudokuMain() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
      cp.setLayout(new BorderLayout());
      cp.add(start, BorderLayout.CENTER);

      // ----- ------Starting Panel Configuration and Layout ---------
      setStartDisabled();
      setDifficultyDisabled();
      start.getUserName().addActionListener(new userNameListener());
      start.getStartButton().addActionListener(new startButtonListener());
      start.getDifficultyButton("easy").addActionListener(new easyButtonListener());
      start.getDifficultyButton("medium").addActionListener(new mediumButtonListener());
      start.getDifficultyButton("hard").addActionListener(new hardButtonListener());
      start.getDifficultyButton("insane").addActionListener(new insaneButtonListener());

      // ----------- MAIN Sudoku Layout and Configuaration --------------
      header = new JPanel();
      header.setLayout(new GridLayout(1, 2));
      lblTime.setFont(new Font("Serif", Font.PLAIN, 80));
      lblTime.setHorizontalAlignment(JLabel.CENTER);
      lblTime.setVerticalAlignment(JLabel.CENTER);
      timer = new Timer(1000, new timeListener());
      header.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
      header.add(lblTime);
      header.setOpaque(false);

      // ----------STATUSBAR (sub of main)----------
      statusBar = new JPanel();
      lblMistakes.setOpaque(false);
      lblMistakes.add(new JTransparentLabel("Mistakes: " + mistakesCount));
      lblMistakes.setBorder(BorderFactory.createEmptyBorder(8, 50, 8, 50));
      statusBar.add(lblMistakes);

      lblCellsLeft = new JLabel("Cells left: " + cellsLeft);
      lblCellsLeft.setBorder(BorderFactory.createEmptyBorder(8, 50, 8, 50));
      statusBar.add(lblCellsLeft);

      lblHintLeft.setBorder(BorderFactory.createEmptyBorder(8, 50, 8, 50));
      statusBar.add(lblHintLeft);
      statusBar.setOpaque(false);

      soundStream = AudioSystem.getAudioInputStream(new File("bg_music_sudoku.wav"));
      bgMusic = AudioSystem.getClip();
      bgMusic.open(soundStream);

      // ----------------MENUBAR-----------------
      menubar.menubar.setEnabled(false);
      menubar.menubar.setVisible(false);
      menubar.newGameItem.addActionListener(new newGameListener());
      menubar.restartGameItem.addActionListener(new resetGameListener());
      menubar.getHintItem.addActionListener(new hintListener());
      menubar.toggleThemeItem.addActionListener(new toggleThemeListener());
      menubar.toggleSoundItem.addActionListener(new toggleSoundListener());
      menubar.aboutItem.addActionListener(new aboutUsListener());
      menubar.instructionsItem.addActionListener(new instructionListener());
      setJMenuBar(menubar.menubar);

      pack(); 
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

   /*---------------------------------- functions ---------------------------------- */
   private void StartGame() {
      setMainLayout();
      board.newGame();
      board.setOpaque(false);

      // Initialize CELLSLEFT
      cellsLeft = board.getCellsLeft();
      lblCellsLeft.setText("Cells left: " + cellsLeft);

      initializeBoxCount();
      initializeBoxFunction();
      recolorFont();
      openInstructions();
      checkGameOver();
      revalidate();
      repaint();

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
      for (int r = 0; r < 9; ++r) {
         for (int c = 0; c < 9; ++c) {
            Cell referenceCell = board.getCell(r, c);
            int cellNumber = referenceCell.number - 1;
            if (referenceCell.status == CellStatus.GIVEN) {
               boxesCount[cellNumber] += 1;
            }
         }
      }
   }

   private void initializeBoxFunction() {
      JButton[] cellBoxesArray = cellBoxesPanel.getCellBoxes();
      for (int i = 0; i < 9; i++) {
         JButton referenceBox = cellBoxesArray[i];
         referenceBox.addFocusListener(new highlightNumberListener());
         referenceBox.setEnabled(true);
      }
   }

   private void recolorFont() {
      lblCellsLeft.setForeground(GameBoardPanel.isDarkMode ? Cell.FG_GIVEN_DARK : Cell.FG_GIVEN_LIGHT);
      lblHintLeft.setForeground(GameBoardPanel.isDarkMode ? Cell.FG_GIVEN_DARK : Cell.FG_GIVEN_LIGHT);
      lblMistakes.setForeground(GameBoardPanel.isDarkMode ? Cell.FG_GIVEN_DARK : Cell.FG_GIVEN_LIGHT);
      lblCellsLeft.setForeground(GameBoardPanel.isDarkMode ? Cell.FG_GIVEN_DARK : Cell.FG_GIVEN_LIGHT);
      lblTime.setForeground(GameBoardPanel.isDarkMode ? Cell.FG_GIVEN_DARK : Cell.FG_GIVEN_LIGHT);

      JButton[] cellBoxesArray = cellBoxesPanel.getCellBoxes();
      for (int i = 0; i < 9; ++i) {
         JButton referenceBox = cellBoxesArray[i];
         referenceBox.setForeground(Cell.FG_GIVEN_LIGHT);
      }
   }

   private void setMainLayout() {
      Container cp = getContentPane();
      // Background Panel of Main Sudoku
      imgPath = GameBoardPanel.isDarkMode ? "bgdark.jpeg" : "bglight.png";
      backcp = new MainBackground(imgPath);
      backcp.setLayout(new BorderLayout());

      topPane.add(header);
      topPane.add(new EmptyPanel());
      topPane.setOpaque(false);
      backcp.add(topPane, BorderLayout.NORTH);

      bottomPane.add(statusBar);
      bottomPane.add(new EmptyPanel());
      bottomPane.setOpaque(false);
      backcp.add(bottomPane, BorderLayout.SOUTH);

      backcp.add(new EmptyPanel(), BorderLayout.WEST);

      centerPanel.setLayout(new GridLayout(1, 2));
      backcp.add(centerPanel, BorderLayout.CENTER);

      JPanel sudokuPane = new JPanel(new BorderLayout());
      sudokuPane.setLayout(new BorderLayout());
      sudokuPane.add(board, BorderLayout.CENTER);
      sudokuPane.add(board);
      centerPanel.add(sudokuPane);

      sudokuPane.setOpaque(false);
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
      centerPanel.setOpaque(false);

      cp.add(backcp);
   }

   private void openInstructions() {
      pauseGameState();
      String message = "<html><body style='width: 250px;'> <h3>Game Rules</h3> <ol><li style='padding-bottom: 5px;'>Each number between 1 and 9 only appear once for every row, column, and 3x3 subgrid.</li> <li style='padding-bottom: 5px;'>If you make 10 mistakes, you lose.</li> <li style='padding-bottom: 5px;'>You are given 3 hints which are assigned randomly.</li> <li style='padding-bottom: 5px;'>Each level differs on the number of cells to guess (Easy: 32, Medium: 45, Hard: 54, Insane: 64).</li> <li>Good luck and have fun playing!</li></ol> </html></body>";
      JOptionPane pane = new JOptionPane(message);
      JDialog dialog = pane.createDialog(getContentPane(), "Instructions");
      dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      dialog.setModal(true);
      dialog.setVisible(true);

      // Get the result from the JOptionPane
      int res = (int) pane.getValue();
      if (res == 0) { // have been clicked
         timer.start();
         beginGameState();
      }
   }

   private void beginGameState() {
      timer.start();
      bgMusic.start();
      bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
      centerPanel.setVisible(true);
      toolsPanel.setVisible(true);
      topPane.setVisible(true);
      bottomPane.setVisible(true);
      menubar.menubar.setEnabled(true);
      menubar.menubar.setVisible(true);
      revalidate();
      repaint();
   }

   private void pauseGameState() {
      timer.stop();
      bgMusic.stop();
      centerPanel.setVisible(false);
      toolsPanel.setVisible(false);
      topPane.setVisible(false);
      bottomPane.setVisible(false);
      menubar.menubar.setEnabled(false);
      menubar.menubar.setVisible(false);
      revalidate();
      repaint();
   }

   private void checkGameOver() {
      for (int r = 0; r < 9; ++r) {
         for (int c = 0; c < 9; ++c) {
            Cell sourceCell = board.getCell(r, c);
            sourceCell.addKeyListener(new checkGameOverListener());
         }
      }
   }

   private void newGameState() {
      for (int row = 0; row < 9; row++) {
         for (int col = 0; col < 9; col++) {
            board.getCell(row, col).disabled = false;
            repaint();
         }
      }
      mistakesCount = 0;
      lblMistakes.setText("Mistakes: " + mistakesCount);
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

   /*---------------------------------- Action Listener ---------------------------------- */
   private class resetGameListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
               board.getCell(row, col).disabled = false;
               repaint();
            }
         }
   
         timer.stop();
         timer.start();
         mistakesCount = 0;
         lblMistakes.setText("Mistakes: " + mistakesCount);
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

   private class newGameListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         newGameState();
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

   private class toggleThemeListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent evt) {
         GameBoardPanel.isDarkMode = !GameBoardPanel.isDarkMode;
         try {
            for (int row = 0; row < 9; row++) {
               for (int col = 0; col < 9; col++) {
                  board.getCell(row, col).paint();
               }
            }
            recolorFont();
            imgPath = GameBoardPanel.isDarkMode ? "bgdark.jpeg" : "bglight.png";
            backcp.setBackgroundImage(imgPath);
            backcp.revalidate();
            backcp.repaint();
         } catch (Exception e) {
            start.setBackgroundImage();
            revalidate();
            repaint();
         }
      }
   }

   private class highlightNumberListener implements FocusListener {
      @Override
      public void focusGained(FocusEvent evt) {
         JButton pressedButton = (JButton) evt.getSource();
         int pressedNumber = Integer.parseInt(pressedButton.getText());
         pressedButton.setForeground(Color.RED);

         for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
               Cell currCell = board.getCell(row, col);
               if (currCell.number == pressedNumber
                     && (currCell.status == CellStatus.GIVEN || currCell.status == CellStatus.CORRECT_GUESS)) {
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

         pressedButton.setForeground(Color.BLACK);
         for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
               Cell currCell = board.getCell(row, col);
               if (currCell.number == pressedNumber) {
                  currCell.mainFocus = false;
                  currCell.focus = false;
                  currCell.paint();
               }
            }
         }
      }
   }

   private class userNameListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent evt) {
         if (!start.getUserName().getText().equals("")) {
            setDifficultyEnabled();
         } else {
            setDifficultyDisabled();
            setStartDisabled();
         }
      }
   }

   private class startButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent evt) {
         cp.remove(start);
         revalidate();
         repaint();
         StartGame();
      }
   }

   private class easyButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent evt) {
         setStartEnabled();
         GameBoardPanel.difficulty = GameDifficulty.EASY;
      }
   }

   private class mediumButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent evt) {
         setStartEnabled();
         GameBoardPanel.difficulty = GameDifficulty.MEDIUM;
      }
   }

   private class hardButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent evt) {
         setStartEnabled();
         GameBoardPanel.difficulty = GameDifficulty.HARD;
      }
   }

   private class insaneButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent evt) {
         setStartEnabled();
         GameBoardPanel.difficulty = GameDifficulty.INSANE;
      }
   }

   private class toggleSoundListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent evt) {
         if (menubar.toggleSoundItem.getText().equals("Disable Sound")) {
            bgMusic.stop();
            menubar.toggleSoundItem.setText("Enable Sound");
         } else {
            bgMusic.start();
            menubar.toggleSoundItem.setText("Disable Sound");
         }

      }
   }

   private class timeListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent evt) {
         seconds++;
         int lblMinute = seconds / 60;
         int lblSecond = seconds % 60;
         lblTime.setText(String.format("%02d:%02d", lblMinute, lblSecond));
      };
   };

   private class aboutUsListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent evt) {
         String message = "<html><body style='width: 250px;'> <p>Dear players,</p> <p></p> <p>Welcome to our Sudoku game, created for our Object Oriented Programming (IM1003) course at NTU. Our team of passionate programmers came together to create an enjoyable Sudoku experience. Our game is intuitive with various difficulty levels, and offers an opportunity to sharpen your problem solving skills. We hope you enjoy playing our Sudoku game and thank you for choosing our game.</p> <p style='padding-top: 30px; padding-bottom: 10px;'>Regards, </p> <p>Developers Team;</p></body></html>";
         JOptionPane.showMessageDialog(getContentPane(), message, "About Us", 1);
      }
   }

   private class instructionListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent evt) {
         openInstructions();
      }
   }

   class checkGameOverListener implements KeyListener {
      @Override
      public void keyTyped(KeyEvent evt) {
         if (board.isGameOver()) {
            SudokuMain.timer.stop();
            gameOverPane = new JOptionPane("You Lost, please start a new game!");
            JDialog dialog = gameOverPane.createDialog(getContentPane(), "Game Over!");
            dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            dialog.setModal(true);
            dialog.setVisible(true);

            int res = (int) gameOverPane.getValue();
            if (res == 0) { 
               newGameState();
            }
         }
      }
      @Override
      public void keyPressed(KeyEvent evt) {
      }
      @Override
      public void keyReleased(KeyEvent evt) {
      }
   }
}