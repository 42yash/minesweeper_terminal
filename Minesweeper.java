import java.util.*;

public class Minesweeper {

    private static final char MINE_CHAR = 'X';
    private static final char UNOPENED_CHAR = '?';
    private static final char FLAG_CHAR = 'X';

    private int totalRows;
    private int totalCols;
    private int mines = totalCols * totalRows / 10 + 1;

    private char[][] board;
    private boolean[][] mineLocations;
    private boolean[][] opened;
    private int remainingUnopened;
    private int remainingMines;
    private boolean gameOver;

    public Minesweeper() {
        printWecomeMessage();
        board = new char[totalRows][totalCols];
        mineLocations = new boolean[totalRows][totalCols];
        opened = new boolean[totalRows][totalCols];
        remainingUnopened = totalRows * totalCols;
        remainingMines = mines;
        gameOver = false;
        initializeBoard();
        initializeMines();
    }

    private void initializeBoard() {
        for (int i = 0; i < totalRows; i++) {
            for (int j = 0; j < totalCols; j++) {
                board[i][j] = UNOPENED_CHAR;
            }
        }
    }

    private void initializeMines() {
        Random random = new Random();
        int minesPlaced = 0;
        while (minesPlaced < mines) {
            int row = random.nextInt(totalRows);
            int col = random.nextInt(totalCols);
            if (!mineLocations[row][col]) {
                mineLocations[row][col] = true;
                minesPlaced++;
            }
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (!gameOver) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            printBoard();
            System.out.print("\n\nEnter row and column (e.g. 2 3): ");
            int row = scanner.nextInt();
            int col = scanner.nextInt();
            row--; // Convert to 0-based index
            col--; // Convert to 0-based index

            if (row < 0 || row >= totalRows || col < 0 || col >= totalCols) {
                System.out.println("Invalid row or column.");
                continue;
            }

            scanner.nextLine(); // Consume the rest of the line
            System.out.print("Open or flag (o/f): ");
            String action = scanner.nextLine();
            if (action.equals("o")) {
                openCell(row, col);
            } else if (action.equals("f")) {
                flagCell(row, col);
            }
            if (remainingUnopened == mines) {
                System.out.println("Congratulations! You won!");
                gameOver = true;
            } else if (gameOver) {
                printBoard();
                System.out.println("Game over.");
            }
        }
        scanner.close();
    }

    private void printWecomeMessage() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("Welcome to Minesweeper!\n");
        System.out.println("To win, open all cells that are not mines.");
        System.out.println("To open a cell, enter the row and column number.");
        System.out.println("To flag a cell, enter the row and column number.");
        System.out.println("Good luck!");
        System.out.println("Press enter to continue...");

        // Wait for user to press enter
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
        printOptions();
    }

    private void printOptions() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        Scanner scanner = new Scanner(System.in);
        int rows;
        int cols;
        int tries = 0;

        while (true) {
            System.out.println("How many rows would you like to play with? (Max 20)");
            rows = scanner.nextInt();
            if (rows > 20 || rows < 1) {
                System.out.println("Please enter a number less than 20");
                continue;
            }
            if(tries==3){
                System.out.println("You have exceeded the number of tries");
                System.out.println("Defaulting to 10 rows.");
                break;
            }
            scanner.close();
        }
        while (true) {
            System.out.println("How many columns would you like to play with? (Max 25");
            cols = scanner.nextInt();
            if (cols > 25 || cols < 1) {
                System.out.println("Please enter a number less than 25");
                continue;
            }
            if (tries == 3) {
                System.out.println("You have exceeded the number of tries");
                System.out.println("Defaulting to 10 columns.");
                break;
            }
        }
        totalRows = rows;
        totalCols = cols;
        mines = totalCols * totalRows / 10 + 1;

    }

    private void printBoard() {
        System.out.println("Minesweeper+\n");
        System.out.println("To Win: Flag all mines. or Open all cells that are not mines.\n");
        System.out.println("You loose if you open a mine or Flag an empty cell\n");
        System.out.println("Remaining unopened cells: " + remainingUnopened);
        System.out.println("Remaining mines: " + remainingMines);

        System.out.print("   ");
        for (int i = 0; i < board[0].length; i++) {
            System.out.printf("%2d ", i + 1);
        }
        System.out.println();

        System.out.print("  +");
        for (int i = 0; i < board[0].length; i++) {
            System.out.print("--+");
        }
        System.out.println();

        // print board contents with left and right grid lines
        for (int i = 0; i < board.length; i++) {
            System.out.printf("%2d|", i + 1);
            for (int j = 0; j < board[0].length; j++) {
                System.out.printf("%2s|", board[i][j]);
            }
            System.out.println();

            // print grid line between rows
            System.out.print("  +");
            for (int j = 0; j < board[0].length; j++) {
                System.out.print("--+");
            }
            System.out.println();
        }
    }

    private void openCell(int row, int col) {
        if (mineLocations[row][col]) {
            System.out.println("You hit a mine!");
            board[row][col] = MINE_CHAR;
            gameOver = true;
            return;
        }
        if (opened[row][col]) {
            System.out.println("Cell already opened.");
            return;
        }
        int numAdjacentMines = getNumAdjacentMines(row, col);
        board[row][col] = (char) ('0' + numAdjacentMines);
        if (numAdjacentMines == 0) {
            board[row][col] = ' ';
        }

        opened[row][col] = true;
        remainingUnopened--;
        if (numAdjacentMines == 0) {
            openAdjacentCells(row, col);
        }
    }

    private void openAdjacentCells(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < totalRows && j >= 0 && j < totalCols && !opened[i][j]) {
                    openCell(i, j);
                }
            }
        }
    }

    private int getNumAdjacentMines(int row, int col) {
        int numAdjacentMines = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < totalRows && j >= 0 && j < totalCols && mineLocations[i][j]) {
                    numAdjacentMines++;
                }
            }
        }
        return numAdjacentMines;
    }

    private void flagCell(int row, int col) {
        if (opened[row][col]) {
            System.out.println("Cell already opened.");
            return;
        }
        if (mineLocations[row][col]) {
            board[row][col] = FLAG_CHAR;
            remainingMines--;
        } else {
            System.out.print("You flagged a cell that doesn't have a mine.");
            board[row][col] = 'X';
            gameOver = true;
        }
    }

    public static void main(String[] args) {
        Minesweeper game = new Minesweeper();
        game.run();
    }
}
