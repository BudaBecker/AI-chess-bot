package UI;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import chess.Color;
import chess.GameState;
import chess.Piece;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessPanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    public static final int tileSize = 100;
    public static final int screenWidth = (tileSize * 8) + 250; // Board + InfoPanel
    public static final int screenHeight = tileSize * 8;
    final int FPS = 165;

    // INITIALIZE THE GAME
    Thread chessGameThread;
    BoardUI boardUI = new BoardUI();
    Color currentColor = Color.WHITE;
    Mouse mouse = new Mouse();

    // PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    Piece activeP;

    // GAME STATE
    boolean gameOver = false;
    String gameResult = "";

    public ChessPanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(java.awt.Color.BLACK);
        this.setDoubleBuffered(true); // improve game's rendering performance

        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        createInitialSetup();
        copyPieces(pieces, simPieces);
    }

    private void createInitialSetup() {
        // WHITES
        pieces.add(new Pawn(0, 6, Color.WHITE));
        pieces.add(new Pawn(1, 6, Color.WHITE));
        pieces.add(new Pawn(2, 6, Color.WHITE));
        pieces.add(new Pawn(3, 6, Color.WHITE));
        pieces.add(new Pawn(4, 6, Color.WHITE));
        pieces.add(new Pawn(5, 6, Color.WHITE));
        pieces.add(new Pawn(6, 6, Color.WHITE));
        pieces.add(new Pawn(7, 6, Color.WHITE));
        pieces.add(new Rook(0, 7, Color.WHITE));
        pieces.add(new Rook(7, 7, Color.WHITE));
        pieces.add(new Knight(1, 7, Color.WHITE));
        pieces.add(new Knight(6, 7, Color.WHITE));
        pieces.add(new Bishop(2, 7, Color.WHITE));
        pieces.add(new Bishop(5, 7, Color.WHITE));
        pieces.add(new Queen(3, 7, Color.WHITE));
        pieces.add(new King(4, 7, Color.WHITE));

        // BLACKS
        pieces.add(new Pawn(0, 1, Color.BLACK));
        pieces.add(new Pawn(1, 1, Color.BLACK));
        pieces.add(new Pawn(2, 1, Color.BLACK));
        pieces.add(new Pawn(3, 1, Color.BLACK));
        pieces.add(new Pawn(4, 1, Color.BLACK));
        pieces.add(new Pawn(5, 1, Color.BLACK));
        pieces.add(new Pawn(6, 1, Color.BLACK));
        pieces.add(new Pawn(7, 1, Color.BLACK));
        pieces.add(new Rook(0, 0, Color.BLACK));
        pieces.add(new Rook(7, 0, Color.BLACK));
        pieces.add(new Knight(1, 0, Color.BLACK));
        pieces.add(new Knight(6, 0, Color.BLACK));
        pieces.add(new Bishop(2, 0, Color.BLACK));
        pieces.add(new Bishop(5, 0, Color.BLACK));
        pieces.add(new Queen(3, 0, Color.BLACK));
        pieces.add(new King(4, 0, Color.BLACK));
    }

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();

        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }

    @Override
    public void run() {

        // Manage FPS
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        // Main game loop
        while (chessGameThread != null) {

            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {

                // Game iteration
                update();
                repaint();

                delta--;
            }
        }
    }

    public void startChessGameThread() {
        chessGameThread = new Thread(this);
        chessGameThread.start();
    }

    public void update() {
        if (gameOver)
            return;

        // MOUSE PRESSED
        if (mouse.pressed) {
            if (activeP == null) {
                for (Piece piece : simPieces) {
                    if (piece.color == currentColor
                            && piece.col == mouse.x / tileSize
                            && piece.row == mouse.y / tileSize) {
                        // Bring selected piece to top of draw order
                        simPieces.remove(piece);
                        simPieces.add(piece);
                        activeP = piece;
                        break;
                    }
                }
            } else {
                simulate();
            }
        }

        // MOUSE RELEASE
        if (!mouse.pressed && activeP != null) {
            int targetCol = activeP.col;
            int targetRow = activeP.row;

            if (isMoveValid(targetCol, targetRow)) {
                // Capture enemy piece if present
                Piece captured = getPieceAt(targetCol, targetRow);
                if (captured != null) {
                    simPieces.remove(captured);
                }
                // Commit the move
                activeP.preCol = targetCol;
                activeP.preRow = targetRow;
                activeP.x = activeP.getPosX();
                activeP.y = activeP.getPosY();
                activeP.moveCount++;
                copyPieces(simPieces, pieces);
                switchTurn();
                checkGameOver();
            } else {
                // Invalid move — reset to origin
                activeP.col = activeP.preCol;
                activeP.row = activeP.preRow;
                activeP.x = activeP.getPosX();
                activeP.y = activeP.getPosY();
                copyPieces(pieces, simPieces);
            }

            activeP = null;
        }
    }

    private void simulate() {
        activeP.x = mouse.x - tileSize / 2;
        activeP.y = mouse.y - tileSize / 2;
        activeP.col = activeP.getCol();
        activeP.row = activeP.getRow();
    }

    private Piece getPieceAt(int col, int row) {
        for (Piece p : simPieces) {
            if (p.col == col && p.row == row) {
                return p;
            }
        }
        return null;
    }

    private boolean isMoveValid(int targetCol, int targetRow) {
        // Must actually move
        if (targetCol == activeP.preCol && targetRow == activeP.preRow)
            return false;
        // Must stay on board
        if (targetCol < 0 || targetCol > 7 || targetRow < 0 || targetRow > 7)
            return false;
        // Cannot land on a friendly piece
        Piece target = getPieceAt(targetCol, targetRow);
        if (target != null && target.color == activeP.color)
            return false;

        // Temporarily restore piece to its origin so legal moves are computed correctly
        activeP.col = activeP.preCol;
        activeP.row = activeP.preRow;
        boolean valid = GameState.generateLegalMoves(activeP, simPieces)[targetCol][targetRow];
        // Restore to target position
        activeP.col = targetCol;
        activeP.row = targetRow;

        return valid;
    }

    private void checkGameOver() {
        if (GameState.isCheckmate(currentColor, pieces)) {
            String winner = (currentColor == Color.WHITE) ? "Black" : "White";
            gameResult = winner + " wins by checkmate!";
            gameOver = true;
        } else if (GameState.isStalemate(currentColor, pieces)) {
            gameResult = "Stalemate — it's a draw!";
            gameOver = true;
        }
    }

    private void switchTurn() {
        currentColor = (currentColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        boardUI.drawBoard(g2);
        for (Piece piece : simPieces) {
            boardUI.drawPiece(g2, piece);
        }

        if (activeP != null) {
            // Mark the position of the current selected piece
            g2.setColor(java.awt.Color.CYAN);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
            g2.fillRect(activeP.preCol * tileSize, activeP.preRow * tileSize, tileSize, tileSize);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            // Overlay the possible future position
            if (mouse.pressed) {
                g2.setColor(java.awt.Color.WHITE);
                g2.setStroke(new BasicStroke(5f));
                g2.drawRect(activeP.col * tileSize + 2, activeP.row * tileSize + 3, tileSize - 5, tileSize - 5);
            }

            // TODO: Mark the possible moves for the current piece

            boardUI.drawPiece(g2, activeP);
        }

        if (gameOver) {
            drawGameOverOverlay(g2);
        }

    }

    private void drawGameOverOverlay(Graphics2D g2) {
        // Dim the board
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f));
        g2.setColor(java.awt.Color.BLACK);
        g2.fillRect(0, 0, tileSize * 8, tileSize * 8);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Result text
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 44));
        java.awt.FontMetrics fm = g2.getFontMetrics();
        int x = (tileSize * 8 - fm.stringWidth(gameResult)) / 2;
        int y = tileSize * 4 - 10;
        g2.setColor(java.awt.Color.YELLOW);
        g2.drawString(gameResult, x, y);

        // Sub-text
        String sub = "New Game coming in Phase 5";
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18));
        fm = g2.getFontMetrics();
        x = (tileSize * 8 - fm.stringWidth(sub)) / 2;
        g2.setColor(java.awt.Color.LIGHT_GRAY);
        g2.drawString(sub, x, y + 44);
    }
}
