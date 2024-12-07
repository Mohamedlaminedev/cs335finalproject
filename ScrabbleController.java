public class ScrabbleController {
	
	

    /**
     * Handles tile selection from rack
     * Corresponds to rack button clicks
     */
    public void selectTileFromRack(int index) {
        List<Character> currentRack = getCurrentRack();
        if (index < currentRack.size()) {
            selectedTile = currentRack.get(index);
            board.updateGameLog("Selected tile: " + selectedTile);
            updateValidPlacements();
        }
    }

    /**
     * Handles tile placement on board
     * Corresponds to board.placeTile() and board.enableSquare()
     */
    public void handleTilePlacement(int row, int col) {
        if (selectedTile == '\0') return;
        
        if (isFirstTurn && (row != 7 || col != 7)) {
            board.updateGameLog("First move must use center square");
            return;
        }

        if (board.isValidSquareToEnable(row, col)) {
            board.placeTile(row, col, selectedTile);
            currentWordTiles.add(new Point(row, col));
            getCurrentRack().remove(Character.valueOf(selectedTile));
            selectedTile = '\0';
            updateDisplay();
        } else {
            board.updateGameLog("Invalid placement");
        }
    }

    /**
     * Updates valid placement squares
     * Uses board.enableSquare() and board.isValidSquareToEnable()
     */
    private void updateValidPlacements() {
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                board.enableSquare(row, col, 
                    isFirstTurn ? (row == 7 && col == 7) : 
                    board.isValidSquareToEnable(row, col));
            }
        }
    }

    /**
     * Handles word submission
     * Uses board.getSquareLetter() and board.updateGameLog()
     */
    public void submitWord() {
        if (currentWordTiles.isEmpty()) {
            board.updateGameLog("No tiles placed");
            return;
        }

        int score = calculateScore();
        if (isPlayer1Turn) {
            player1Score += score;
        } else {
            player2Score += score;
        }

        isFirstTurn = false;
        switchTurn();
        currentWordTiles.clear();
        
        board.updateGameLog("Word submitted! Score: " + score);
        board.updateScores(player1Score, player2Score);
        updateDisplay();
    }

    /**
     * Handles rack shuffling
     * Updates board.setRackTiles()
     */
    public void shuffleRack() {
        Collections.shuffle(getCurrentRack());
        updateDisplay();
        board.updateGameLog("Rack shuffled");
    }

    /**
     * Handles turn skipping
     * Updates display and game log
     */
    public void skipTurn() {
        switchTurn();
        board.updateGameLog(isPlayer1Turn ? "Player 1's turn" : "Player 2's turn");
        updateDisplay();
    }

    /**
     * Updates all display elements
     */
    private void updateDisplay() {
        board.setRackTiles(getCurrentRack());
        updateValidPlacements();
    }

    private List<Character> getCurrentRack() {
        return isPlayer1Turn ? player1Rack : player2Rack;
    }

    private void switchTurn() {
        isPlayer1Turn = !isPlayer1Turn;
        selectedTile = '\0';
    }
}