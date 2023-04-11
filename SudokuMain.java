import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import java.awt.BorderLayout;
import java.util.Arrays;

/**
 * The main Sudoku program
 */

public class SudokuMain extends JFrame {
   private static final long serialVersionUID = 1L; // to prevent serial warning

   // private variables
   GameBoardPanel board = new GameBoardPanel();
   JPanel sidebar, header, statusBar;
   RightPane rightpane;
   BoxesLeft cellBoxesPanel;
   MainBackground backcp;
   JLabel lblTime = new JLabel("00:00"), lblHintLeft = new JLabel("Hint Left: " + 3),
         lblCellsLeft, lblMistakes = new JLabel("Mistakes: " + 0);
   int hintCount = 3, cellsLeft, mistakesCount = 0;
   int[] boxesCount = new int[9];
   StartingPanel start = new StartingPanel();
   // DifficultyPanel diff = new DifficultyPanel();
   GridBagConstraints gbc = new GridBagConstraints();
   topAndBottomPane topPane = new topAndBottomPane(), bottomPane = new topAndBottomPane();
   MenuBar menubar = new MenuBar();
   AudioInputStream soundStream;

   private Clip bgMusic = null;
   private int seconds = 0;
   private String imgPath;
   public static Timer timer;

   // Constructor
   public SudokuMain() throws UnsupportedAudioFileException, IOException, LineUnavailableException {

      Container cp = getContentPane();
      cp.setLayout(new BorderLayout());
      cp.add(start, BorderLayout.CENTER);

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

      // CODE MAIN //
      // ----------header - mistakes count and time count-----------
      header = new JPanel();
      header.setLayout(new GridLayout(1, 2));

      // time count
      lblTime.setFont(new Font("Serif", Font.PLAIN, 80));
      lblTime.setHorizontalAlignment(JLabel.CENTER);
      lblTime.setVerticalAlignment(JLabel.CENTER);

      header.add(lblTime);
      timer = new Timer(1000, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            seconds++;
            int lblMinute = seconds / 60;
            int lblSecond = seconds % 60;
            lblTime.setText(String.format("%02d:%02d", lblMinute, lblSecond));
         }
      });

      // ------------------sidebar - re-start and reset button-----------------
      // toggle theme menu
      menubar.toggleThemeItem.addActionListener(new toggleTheme());

      // ---------- status bar -----------
      statusBar = new JPanel();
      lblCellsLeft = new JLabel("Cells left: " + cellsLeft);

      lblMistakes.setOpaque(false);
      lblMistakes.add(new JTransparentLabel("Mistakes: " + mistakesCount));
      for (int row = 0; row < GameBoardPanel.GRID_SIZE; ++row) {
         for (int col = 0; col < GameBoardPanel.GRID_SIZE; ++col) {
            Cell referenceCell = board.getCell(row, col);
            if (referenceCell.isEditable()) {
               referenceCell.addKeyListener(new mistakeListener());
               referenceCell.addKeyListener(new boxLeftListener());
            }
         }
      }

      statusBar.add(lblMistakes);
      statusBar.add(lblCellsLeft);
      statusBar.add(lblHintLeft);
      lblMistakes.setBorder(BorderFactory.createEmptyBorder(8, 50, 8, 50));
      lblCellsLeft.setBorder(BorderFactory.createEmptyBorder(8, 50, 8, 50));
      lblHintLeft.setBorder(BorderFactory.createEmptyBorder(8, 50, 8, 50));

      // add background music (when game started)
      soundStream = AudioSystem.getAudioInputStream(new File("bg_music_sudoku.wav"));
      bgMusic = AudioSystem.getClip();
      bgMusic.open(soundStream);

      // add menu bar
      setJMenuBar(menubar.menubar);

      // template for Frame
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
      // disable
      imgPath = GameBoardPanel.isDarkMode ? "bgdark.jpeg" : "bglight.png";
      backcp = new MainBackground(imgPath);
      backcp.setLayout(new BorderLayout());

      // top and bottom pane
      topPane.add(header);
      topPane.add(new EmptyPanel());
      backcp.add(topPane, BorderLayout.NORTH);
      backcp.add(new EmptyPanel(), BorderLayout.WEST);
      bottomPane.add(statusBar);
      bottomPane.add(new EmptyPanel());
      backcp.add(bottomPane, BorderLayout.SOUTH);

      // for sudoku pane and empty pane
      JPanel mainScreen = new JPanel();
      mainScreen.setLayout(new GridLayout(1, 2));
      backcp.add(mainScreen, BorderLayout.CENTER);

      // adding sudoku pane to left half of center Pane
      JPanel sudokuPane = new JPanel(new BorderLayout());
      sudokuPane.setOpaque(false);
      sudokuPane.setLayout(new BorderLayout());
      sudokuPane.add(board, BorderLayout.CENTER);
      sudokuPane.add(board);
      mainScreen.add(sudokuPane);

      // right pane - half right of the screen,include the sidebar
      rightpane = new RightPane();
      sidebar = rightpane.getSideBar();
      cellBoxesPanel = new BoxesLeft();
      sidebar.add(new EmptyPanel());
      sidebar.add(cellBoxesPanel);
      sidebar.add(new EmptyPanel());
      mainScreen.add(rightpane);

      // header allignment
      header.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
      board.newGame();
      cellsLeft = board.getCellsLeft();
      lblCellsLeft.setText("Cells Left: " + cellsLeft);
      timer.start();
      bgMusic.setFramePosition(0);
      bgMusic.start();
      bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
      initizializeBoxCount();
      recolorFont();
      // to check
      System.out.println(Arrays.toString(boxesCount));

      cp.add(backcp);

      // set components to opaque
      topPane.setOpaque(false);
      bottomPane.setOpaque(false);
      sidebar.setOpaque(false);
      header.setOpaque(false);
      cellBoxesPanel.setOpaque(false);
      rightpane.setOpaque(false);
      statusBar.setOpaque(false);
      mainScreen.setOpaque(false);
      board.setOpaque(false);

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

   private void initizializeBoxCount() {
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

   public void recolorFont() {
      if (GameBoardPanel.isDarkMode) {
         lblCellsLeft.setForeground(Color.white);
         lblHintLeft.setForeground(Color.white);
         lblMistakes.setForeground(Color.white);
         lblCellsLeft.setForeground(Color.white);
         lblTime.setForeground(Color.white);

         JTextField[] cellBoxesArray = cellBoxesPanel.getCellBoxes();
         for (int i=0; i<9; ++i) {
            JTextField referenceBox = cellBoxesArray[i];
            referenceBox.setForeground(Color.white);
            referenceBox.setBackground(new Color(121,121,121));
         }

      } else {
         lblTime.setForeground(Color.black);
         lblHintLeft.setForeground(Color.black);
         lblMistakes.setForeground(Color.black);
         lblCellsLeft.setForeground(Color.black);
         lblTime.setForeground(Color.black);

         JTextField[] cellBoxesArray = cellBoxesPanel.getCellBoxes();
         for (int i=0; i<9; ++i) {
            JTextField referenceBox = cellBoxesArray[i];
            referenceBox.setForeground(Color.black);
            referenceBox.setBackground(new Color(242, 232, 196));
         }
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
            lblHintLeft.setText("" + hintCount);
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
         imgPath = GameBoardPanel.isDarkMode ? "bgdark.jpeg" : "bglight.png";
         backcp.setBackgroundImage(imgPath);
         backcp.revalidate();
         backcp.repaint();
      }
   }

   // TODO Cellstatus won't update
   private class mistakeListener implements KeyListener {
      @Override
      public void keyTyped(KeyEvent evt) {
         Cell sourceCell = (Cell) evt.getSource();
         System.out.println(sourceCell.number);

         char key = evt.getKeyChar();
         if (!sourceCell.isEditable())
            return;

         if ((key - '0' > 0 && key - '0' <= 9)) {
            int numberIn = key - '0';
            if (numberIn == sourceCell.number) {
               sourceCell.status = CellStatus.CORRECT_GUESS;
               cellsLeft--;
               lblCellsLeft.setText("Cells Left: " + cellsLeft);
               sourceCell.setEditable(false);
            } else {
               sourceCell.status = CellStatus.WRONG_GUESS;
               ++mistakesCount;
               lblMistakes.setText("Mistakes: " + mistakesCount);
            }
            numberIn = numberIn % 10;
            sourceCell.setText(Integer.toString(numberIn));
            evt.consume();
            sourceCell.paint();
         } else if (key == 8) {
            // if input is backspace
            sourceCell.status = CellStatus.TO_GUESS;
            sourceCell.paint();
         } else {
            System.out.println("Invalid input");
            evt.consume();
         }
      }

      @Override
      public void keyPressed(KeyEvent evt) {
      }

      @Override
      public void keyReleased(KeyEvent evt) {
      }
   }

   // TODO cell wont lower opacity even though cell count == 9
   private class boxLeftListener implements KeyListener {
      @Override
      public void keyTyped(KeyEvent evt) {
         Cell sourceCell = (Cell) evt.getSource();
         char key = evt.getKeyChar();
         if (!sourceCell.isEditable())
            return;

         int numberIn = key - '0';
         if (numberIn == sourceCell.number) {
            boxesCount[numberIn] += 1;
            System.out.println(boxesCount);
         } 
         
         // check if any has reached 9 yet
         for (int boxNumber =0; boxNumber < 9; ++boxNumber) {
            if (boxesCount[boxNumber] == 9) {
               // iterate through all the boxes
               JTextField[] cellBoxesArray = cellBoxesPanel.getCellBoxes();
               for (int i=0; i<9; ++i) {
                  JTextField referenceBox = cellBoxesArray[i];
                  if (Integer.parseInt(referenceBox .getText())-1 == boxNumber) {
                     referenceBox.setOpaque(false); // Set the text field to be transparent
                     referenceBox.setBackground(new Color(0,0,0,128)); // Set the background color with an alpha value of 128 (out of 255)
                  }
               }
            }
         }
      }

      @Override
      public void keyPressed(KeyEvent evt) {
         // handle key pressed event here
      }

      @Override
      public void keyReleased(KeyEvent evt) {
         // handle key released event here
      }
   }
}