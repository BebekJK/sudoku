
   /**
    * The Sudoku number puzzle to be solved
    */
   public class Puzzle {
      // All variables have package access
      // The numbers on the puzzle
      int[][] numbers = new int[GameBoardPanel.GRID_SIZE][GameBoardPanel.GRID_SIZE];
      // The clues - isGiven (no need to guess) or need to guess
      boolean[][] isGiven = new boolean[GameBoardPanel.GRID_SIZE][GameBoardPanel.GRID_SIZE];
      boolean isSolveable;
      // Constructor
      public Puzzle() {
         super();
      }

      // Generate a new puzzle given the number of cells to be guessed, which can be used
      //  to control the difficulty level.
      // This method shall set (or update) the arrays numbers and isGiven
      public void newPuzzle(int cellsToGuess) {
         // I hardcode a puzzle here for illustration and testing.
         int[][] hardcodedNumbers =
            {{5, 3, 4, 6, 7, 8, 9, 1, 2},
            {6, 7, 2, 1, 9, 5, 3, 4, 8},
            {1, 9, 8, 3, 4, 2, 5, 6, 7},
            {8, 5, 9, 7, 6, 1, 4, 2, 3},
            {4, 2, 6, 8, 5, 3, 7, 9, 1},
            {7, 1, 3, 9, 2, 4, 8, 5, 6},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 9}};
         
         // Generating sudoku puzzle by predefining 17 initial cells (and then backtrack)
         // Remove 54 random cells from the predefined board
         
         for(int count=1; count<=54; count++){
            int row, col;

            do{
               row = (int)(Math.random()*GameBoardPanel.GRID_SIZE);
               col = (int)(Math.random()*GameBoardPanel.GRID_SIZE);
            }while(hardcodedNumbers[row][col] == 0);

            hardcodedNumbers[row][col] = 0;
         }
         
         // Solving the sudoku using backtracking (in order to generate sudoku solution)
         Boolean isSolveable = solve(hardcodedNumbers,0,0);
         System.out.println(isSolveable? "Puzzle is Solveable": "Puzzle is Unsolveable");
         for(int row=0; row<GameBoardPanel.GRID_SIZE; ++row){
            for(int col=0; col<GameBoardPanel.GRID_SIZE; ++col){
               numbers[row][col] = hardcodedNumbers[row][col];
            }
         }

         //for checking purpose only:
         printSolution(hardcodedNumbers);

         // Need to use input parameter cellsToGuess!
         boolean[][] hardcodedIsGiven =
            {{true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true}};


         // Randomly assign "81 - cellsToGuess" amount of given values
         for(int cnt=0; cnt<cellsToGuess; cnt++){
            int row, col;
            do{
               row = (int)(Math.random() * GameBoardPanel.GRID_SIZE);
               col = (int)(Math.random() * GameBoardPanel.GRID_SIZE);
            }while(hardcodedIsGiven[row][col] == false);
            hardcodedIsGiven[row][col] = false;
         }

         // Copy from hardcodedIsGiven into array "isGiven"
         for (int row = 0; row < GameBoardPanel.GRID_SIZE; ++row) {
            for (int col = 0; col < GameBoardPanel.GRID_SIZE; ++col) {
               isGiven[row][col] = hardcodedIsGiven[row][col];
            }
         }
      }

      private static boolean solve(int[][] grid, int row, int col){
         if(col == GameBoardPanel.GRID_SIZE){
            col = 0;
            row++;
         }
         if(row == GameBoardPanel.GRID_SIZE)return true;
         if(grid[row][col] != 0) return solve(grid, row, col+1);

         for(int num=1; num<=9; num++){
            grid[row][col] = num;
            if(!isValid(grid, row, col))continue;
            if(solve(grid, row, col+1)) return true;
         }
         grid[row][col] = 0;
         return false;
      }

      private static boolean isValid(int[][] grid, int row, int col){
         for(int c=0;c<GameBoardPanel.GRID_SIZE; c++){
            if(c == col)continue;
            if(grid[row][c] == grid[row][col])return false;
         }

         for(int r=0;r<GameBoardPanel.GRID_SIZE; r++){
            if(r == row)continue;
            if(grid[r][col] == grid[row][col])return false;
         }

         for(int r=row/3*3; r<row/3*3+3; r++){
            for(int c=col/3*3; c<col/3*3+3; c++){
               if(r==row && c==col)continue;
               if(grid[r][c] == grid[row][col])return false;
            }
         }
         return true;
      }

      // For checking purpose
      private void printSolution(int[][] grid){
         for(int row=0; row < GameBoardPanel.GRID_SIZE; row++){
            for(int col=0; col<GameBoardPanel.GRID_SIZE; col++){
               System.out.print(grid[row][col] + " ");
            }
            System.out.println();
         }
      }
      //(For advanced students) use singleton design pattern for this class
   }