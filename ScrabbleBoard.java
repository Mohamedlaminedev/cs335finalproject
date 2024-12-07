import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;



public class ScrabbleBoard extends JPanel {
	
	private boolean isFirstTurn = true;  // Tracks if it's the first turn of the game
    private static final int BOARD_SIZE = 15;
    private JButton[][] squares = new JButton[BOARD_SIZE][BOARD_SIZE];
    private JButton selectedTile = null;
    private boolean isPlacingTile = false;
    private List<Point> currentWordTiles = new ArrayList<>();
    private JPanel rackPanel;
    private JTextArea gameLog;
    private JPanel scorePanel;

    public ScrabbleBoard() {
        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        initializeBoard();
        setPreferredSize(new Dimension(600, 600));
    }

    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                squares[row][col] = new JButton();
                squares[row][col].setOpaque(true);
                setDefaultSquareColor(row, col);
                add(squares[row][col]);
            }
        }
    }

    public void setDefaultSquareColor(int row, int col) {
        if ((row == 1 || row == 13) && (col == 5 || col == 9) || 
            (row == 5 || row == 9) && (col == 1 || col == 13)) {
            squares[row][col].setBackground(Color.BLUE);
        }
        else if ((row == 3 || row == 11 || row==7) && (col == 0 || col == 7 || col == 14 || col==3||col==11) || 
                 (row == 6 || row == 8) && (col == 2 || col == 6 || col == 8 || col == 12) || 
                 (row == 0 || row == 14) && (col == 3 || col == 11) || 
                 (row == 2 || row == 12) && (col == 6 || col == 8)) {
            squares[row][col].setBackground(Color.CYAN);
        }
        else {
            squares[row][col].setBackground(Color.WHITE);
        }
    }

    // GUI Control Methods
   

    public void placeTile(int row, int col, char letter) {
        squares[row][col].setText(String.valueOf(letter));
        
    }

    public void clearSquare(int row, int col) {
        squares[row][col].setText("");
    }

    public String getSquareLetter(int row, int col) {
        return squares[row][col].getText();
    }

    public void highlightSquare(int row, int col) {
        squares[row][col].setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
    }

    public void unhighlightSquare(int row, int col) {
        squares[row][col].setBorder(UIManager.getBorder("Button.border"));
    }

    public void setRackTiles(List<Character> tiles) {
        Component[] rackComponents = rackPanel.getComponents();
        for (int i = 0; i < rackComponents.length && i < tiles.size(); i++) {
            if (rackComponents[i] instanceof JButton) {
                ((JButton)rackComponents[i]).setText(String.valueOf(tiles.get(i)));
            }
        }
    }
    /**
     * Enables/disables board squares with visual feedback
     * @param row Row position on board
     * @param col Column position on board
     * @param enable Whether to enable the square
     */
    public void enableSquare(int row, int col, boolean enable) {
        squares[row][col].setEnabled(enable);
        
        if (enable) {
            squares[row][col].setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            squares[row][col].setBackground(new Color(230, 255, 230)); // Light green
        } else {
            squares[row][col].setBorder(UIManager.getBorder("Button.border"));
            setDefaultSquareColor(row, col);
        }
    }


    // Checks if a square should be enabled based on Scrabble rules:
    // 1. First turn must use center square
    // 2. Other turns must connect to existing words
    // 3. Can only place in empty squares
    public boolean isValidSquareToEnable(int row, int col) {
        // Special case: first turn must use center square
        if (isFirstTurn && row == 7 && col == 7) {
            return true;
        }
        
        // Can't place tile on an occupied square
        if (!squares[row][col].getText().isEmpty()) {
            return false;
        }
        
        // Check if there's at least one adjacent tile
        boolean hasAdjacentTile = false;
        
        // Look in all four directions (up, down, left, right)
        // Check above if not in top row
        if (row > 0 && !squares[row-1][col].getText().isEmpty()) {
            hasAdjacentTile = true;
        }
        // Check below if not in bottom row
        if (row < BOARD_SIZE-1 && !squares[row+1][col].getText().isEmpty()) {
            hasAdjacentTile = true;
        }
        // Check left if not in leftmost column
        if (col > 0 && !squares[row][col-1].getText().isEmpty()) {
            hasAdjacentTile = true;
        }
        // Check right if not in rightmost column
        if (col < BOARD_SIZE-1 && !squares[row][col+1].getText().isEmpty()) {
            hasAdjacentTile = true;
        }
        
        return hasAdjacentTile;
    }


    public void updateGameLog(String message) {
        gameLog.append(message + "\n");
    }

    public void updateScores(int player1Score, int player2Score) {
        Component[] components = scorePanel.getComponents();
        if (components[0] instanceof JLabel) {
            ((JLabel)components[0]).setText("Player 1 Score: " + player1Score);
        }
        if (components[1] instanceof JLabel) {
            ((JLabel)components[1]).setText("Player 2 Score: " + player2Score);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Scrabble");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        ScrabbleBoard board = new ScrabbleBoard();
        frame.add(board, BorderLayout.CENTER);

        JPanel rackPanel = new JPanel();
        rackPanel.setLayout(new GridLayout(1, 7));
        for (int i = 0; i < 7; i++) {
            JButton tile = new JButton(" ");
            tile.setFont(new Font("Arial", Font.BOLD, 16));
            rackPanel.add(tile);
        }
        frame.add(rackPanel, BorderLayout.SOUTH);
        board.rackPanel = rackPanel;

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(200, 600));

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(4, 1));
        
        JButton submitButton = new JButton("Submit Word");
        JButton shuffleButton = new JButton("Shuffle Rack");
        JButton skipTurnButton = new JButton("Skip Turn");
        JButton endGameButton = new JButton("End Game");

        submitButton.setMargin(new Insets(10, 10, 10, 10));
        shuffleButton.setMargin(new Insets(10, 10, 10, 10));
        skipTurnButton.setMargin(new Insets(10, 10, 10, 10));
        endGameButton.setMargin(new Insets(10, 10, 10, 10));

        controlPanel.add(submitButton);
        controlPanel.add(shuffleButton);
        controlPanel.add(skipTurnButton);
        controlPanel.add(endGameButton);

        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidePanel.add(controlPanel, BorderLayout.NORTH);

        JTextArea gameLog = new JTextArea();
        gameLog.setEditable(false);
        gameLog.setLineWrap(true);
        gameLog.setWrapStyleWord(true);
        gameLog.setBorder(BorderFactory.createLineBorder(Color.GRAY,2));
        sidePanel.add(gameLog, BorderLayout.CENTER);
        board.gameLog = gameLog;
        
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(2, 1));
        scorePanel.add(new JLabel("Player 1 Score: "));
        scorePanel.add(new JLabel("Player 2 Score: "));
        sidePanel.add(scorePanel, BorderLayout.SOUTH);
        board.scorePanel = scorePanel;

        frame.add(sidePanel, BorderLayout.EAST);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}