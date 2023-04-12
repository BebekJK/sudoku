package sudoku;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class GameBoardPanel extends JPanel {
   private static final long serialVersionUID = 1L; // to prevent serial warning

   public static Boolean isDarkMode = false;
   // Define named constants for the game board properties
   public static final int GRID_SIZE = 9; // Size of the board
   public static final int SUBGRID_SIZE = 3; // Size of the sub-grid
   // Define named constants for UI sizes
   public static final int CELL_SIZE = 60; // Cell width/height in pixels
   public static final int BOARD_WIDTH = CELL_SIZE * GRID_SIZE;
   public static final int BOARD_HEIGHT = CELL_SIZE * GRID_SIZE;
   // Board width/height in pixels
   // Define properties
   /** The game board composes of 9x9 Cells (customized JTextFields) */
   private Cell[][] cells = new Cell[GRID_SIZE][GRID_SIZE];
   /** It also contains a Puzzle with array numbers and isGiven */
   private Puzzle puzzle = new Puzzle();

   public static GameDifficulty difficulty;
   private int cellsToGuess;


   /** Constructor */
   public GameBoardPanel() {
      super.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE, 2, 2)); // JPanel

      // Allocate the 2D array of Cell, and added into JPanel.
      for (int row = 0; row < GRID_SIZE; ++row) {
         for (int col = 0; col < GRID_SIZE; ++col) {
            cells[row][col] = new Cell(row, col);
            super.add(cells[row][col]); // JPanel
         }
      }

      for (int row = 0; row < GRID_SIZE; ++row) {
         for (int col = 0; col < GRID_SIZE; ++col) {

            if (cells[row][col].isEditable()) {
               // cells[row][col].addActionListener(listener); // For all editable rows and
               // cols
               cells[row][col].addKeyListener(new KeyCellInputListener());
               cells[row][col].addMouseListener(new mouseCellInputListener());
               cells[row][col].addFocusListener(new CellFocusListener());
            }
         }
      }


      super.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
   }

   /**
    * Generate a new puzzle; and reset the gameboard of cells based on the puzzle.
    * You can call this method to start a new game.
    */
   public void newGame() {
      // Generate a new puzzle

      if (difficulty == GameDifficulty.EASY)
         cellsToGuess = 32;
      else if (difficulty == GameDifficulty.MEDIUM)
         cellsToGuess = 45;
      else if (difficulty == GameDifficulty.HARD)
         cellsToGuess = 54;
      else
         cellsToGuess = 64;

      puzzle.newPuzzle(cellsToGuess);

      // Initialize all the 9x9 cells, based on the puzzle.
      for (int row = 0; row < GRID_SIZE; ++row) {
         for (int col = 0; col < GRID_SIZE; ++col) {
            cells[row][col].newGame(puzzle.numbers[row][col], puzzle.isGiven[row][col]);
         }
      }

      for (int row = 0; row < GRID_SIZE; ++row) {
         for (int col = 0; col < GRID_SIZE; ++col) {
            if (cells[row][col].isEditable()) {
               System.out.println(row + ", " + col);
               cells[row][col].requestFocus();
               return;
            }
         }
      }
   }

   /**
    * Return true if the puzzle is solved
    * i.e., none of the cell have status of TO_GUESS or WRONG_GUESS
    */
   public boolean isSolved() {
      return SudokuMain.cellsLeft == 0;
   }

   public boolean isGameOver(){
      return SudokuMain.mistakesCount == 10;
   }

   // getter method
   public Cell getCell(int row, int col) {
      return cells[row][col];
   }

   public int getCellsLeft() {
      return cellsToGuess;
   }

   private Boolean isValidInput(char c) {
      return (c - '0' > 0 && c - '0' <= 9);
   }
   
   private void disableBoard(){
      for(int row=0; row<9; row++){
         for(int col=0; col<9; col++){
            cells[row][col].disabled = true;
            cells[row][col].paint();
         }
      }
   }
   class KeyCellInputListener implements KeyListener {
      @Override
      public void keyTyped(KeyEvent evt) {
         Cell sourceCell = (Cell) evt.getSource();

         char key = evt.getKeyChar();
         if (!sourceCell.isEditable())
            return;

         if (isValidInput(key)) {
            int numberIn = key - '0';
            if (numberIn == sourceCell.number) {
               sourceCell.status = CellStatus.CORRECT_GUESS;
               sourceCell.setEditable(false);

               SudokuMain.cellsLeft--;
               SudokuMain.lblCellsLeft.setText("Cells Left: " + SudokuMain.cellsLeft);

               SudokuMain.boxesCount[numberIn-1]++;

            } else {
               sourceCell.status = CellStatus.WRONG_GUESS;

               SudokuMain.mistakesCount++;
               SudokuMain.lblMistakes.setText("Mistakes: " + SudokuMain.mistakesCount);
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

         for (int boxNumber =0; boxNumber < 9; ++boxNumber) {
            if (SudokuMain.boxesCount[boxNumber] == 9) {
               JButton[] cellBoxesArray = SudokuMain.cellBoxesPanel.getCellBoxes();
               // cellBoxesArray[boxNumber].setBackground(Color.BLACK);
               cellBoxesArray[boxNumber].setEnabled(false);
            }
         }
         if(isSolved()){
            SudokuMain.timer.stop();
            JOptionPane.showMessageDialog(null, "Congratulations, Puzzle Solved!");
         }
         if(isGameOver()){
            SudokuMain.timer.stop();
            JOptionPane.showMessageDialog(null , "You Lost, please start a new game!");
            disableBoard();
         }
      }

      @Override
      public void keyPressed(KeyEvent evt) {
         Cell sourceCell = (Cell) evt.getSource();
         int key = evt.getKeyCode();
         int row = sourceCell.row;
         int col = sourceCell.col;

         cells[row][col].focus = false;

         if (key == KeyEvent.VK_LEFT) {
            cells[row][(col + 8) % 9].requestFocus();
         } else if (key == KeyEvent.VK_RIGHT) {
            cells[row][(col + 1) % 9].requestFocus();
         } else if (key == KeyEvent.VK_UP) {
            cells[(row + 8) % 9][col].requestFocus();
         } else if (key == KeyEvent.VK_DOWN) {
            cells[(row + 1) % 9][col].requestFocus();
         }
         sourceCell.paint();
      }

      @Override
      public void keyReleased(KeyEvent evt) {
         Cell sourceCell = (Cell) evt.getSource();
         sourceCell.paint();
      }
   }

   private class mouseCellInputListener implements MouseListener {
      @Override
      public void mouseClicked(MouseEvent evt) {
         Cell sourceCell = (Cell) evt.getSource();
         sourceCell.requestFocus();
      }

      // Not used - need to provide an empty body to compile.
      @Override
      public void mousePressed(MouseEvent evt) {
      }

      @Override
      public void mouseReleased(MouseEvent evt) {
      }

      @Override
      public void mouseEntered(MouseEvent evt) {
      }

      @Override
      public void mouseExited(MouseEvent evt) {
      }
   }

   private class CellFocusListener implements FocusListener {
      @Override
      public void focusGained(FocusEvent evt) {
         Cell sourceCell = (Cell) evt.getComponent();
         sourceCell.focus = true;
         sourceCell.paint();

         int currRow = sourceCell.row;
         int currCol = sourceCell.col;
         cells[currRow][currCol].mainFocus = true;
         for (int row = 0; row < 9; row++) {
            cells[row][currCol].focus = true;
            cells[row][currCol].paint();
         }
         for (int col = 0; col < 9; col++) {
            cells[currRow][col].focus = true;
            cells[currRow][col].paint();
         }
         for (int row = currRow / 3 * 3; row < currRow / 3 * 3 + 3; ++row) {
            for (int col = currCol / 3 * 3; col < currCol / 3 * 3 + 3; ++col) {
               cells[row][col].focus = true;
               cells[row][col].paint();
            }
         }
      }

      @Override
      public void focusLost(FocusEvent evt) {
         Cell sourceCell = (Cell) evt.getComponent();
         sourceCell.focus = false;
         sourceCell.paint();

         int currRow = sourceCell.row;
         int currCol = sourceCell.col;
         cells[currRow][currCol].mainFocus = false;
         for (int row = 0; row < 9; row++) {
            cells[row][currCol].focus = false;
            cells[row][currCol].paint();
         }
         for (int col = 0; col < 9; col++) {
            cells[currRow][col].focus = false;
            cells[currRow][col].paint();
         }
         for (int row = currRow / 3 * 3; row < currRow / 3 * 3 + 3; ++row) {
            for (int col = currCol / 3 * 3; col < currCol / 3 * 3 + 3; ++col) {
               cells[row][col].focus = false;
               cells[row][col].paint();
            }
         }
      }
   }

   
}