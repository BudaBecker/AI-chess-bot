package UI;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import chess.AlgebraicNotation;
import chess.Color;
import chess.GameState;
import chess.Move;
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

    // MOVE HISTORY — lastMove is static so Pawn can access it for en passant
    public static Move lastMove = null;
    ArrayList<Move> moveHistory = new ArrayList<>();

    // CAPTURED PIECES
    ArrayList<Piece> capturedByWhite = new ArrayList<>(); // pieces white has taken
    ArrayList<Piece> capturedByBlack = new ArrayList<>(); // pieces black has taken

    // LEGAL MOVE HINTS — cached when a piece is picked up
    boolean[][] legalMovesGrid = null;

    // PHASE 6 — polish
    boolean boardFlipped = false;
    SoundManager sounds = SoundManager.getInstance();

    /** Screen pixel x-offset for a given board column (accounts for flip). */
    private int scx(int col) { return boardFlipped ? (7 - col) * tileSize : col * tileSize; }
    /** Screen pixel y-offset for a given board row (accounts for flip). */
    private int scy(int row) { return boardFlipped ? (7 - row) * tileSize : row * tileSize; }
    /** Convert mouse screen column → board column. */
    private int boardCol(int mouseX) {
        int sc = mouseX / tileSize;
        return boardFlipped ? 7 - sc : sc;
    }
    /** Convert mouse screen row → board row. */
    private int boardRow(int mouseY) {
        int sr = mouseY / tileSize;
        return boardFlipped ? 7 - sr : sr;
    }

    public ChessPanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(java.awt.Color.BLACK);
        setDoubleBuffered(true);
        setLayout(null); // absolute layout for buttons

        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        createInitialSetup();
        copyPieces(pieces, simPieces);
        setupButtons();
        setupKeyBindings();
    }

    private void setupButtons() {
        int bx = tileSize * 8 + 20;
        int bw = 210;

        JButton flipBtn = new JButton("Flip Board");
        flipBtn.setBounds(bx, screenHeight - 220, bw, 36);
        flipBtn.setFocusable(false);
        flipBtn.addActionListener(e -> { boardFlipped = !boardFlipped; repaint(); });
        add(flipBtn);

        JButton themeBtn = new JButton("Toggle Theme");
        themeBtn.setBounds(bx, screenHeight - 170, bw, 36);
        themeBtn.setFocusable(false);
        themeBtn.addActionListener(e -> {
            BoardUI.BoardTheme next = (boardUI.getTheme() == BoardUI.BoardTheme.GRAY)
                    ? BoardUI.BoardTheme.BROWN : BoardUI.BoardTheme.GRAY;
            boardUI.setTheme(next);
            repaint();
        });
        add(themeBtn);

        JButton undoBtn = new JButton("Undo (Ctrl+Z)");
        undoBtn.setBounds(bx, screenHeight - 120, bw, 36);
        undoBtn.setFocusable(false);
        undoBtn.addActionListener(e -> undoLastMove());
        add(undoBtn);

        JButton newGameBtn = new JButton("New Game");
        newGameBtn.setBounds(bx, screenHeight - 70, bw, 36);
        newGameBtn.setFocusable(false);
        newGameBtn.addActionListener(e -> resetGame());
        add(newGameBtn);
    }

    @SuppressWarnings("serial")
    private void setupKeyBindings() {
        bind(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK, "undo",    e -> undoLastMove());
        bind(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK, "newGame", e -> resetGame());
        bind(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK, "flip",    e -> { boardFlipped = !boardFlipped; repaint(); });
        bind(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK, "pgn",     e -> exportPgn());
    }

    private void bind(int key, int mask, String name, java.awt.event.ActionListener action) {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(key, mask), name);
        getActionMap().put(name, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { action.actionPerformed(e); }
        });
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
                int clickCol = boardCol(mouse.x);
                int clickRow = boardRow(mouse.y);
                for (Piece piece : simPieces) {
                    if (piece.color == currentColor
                            && piece.col == clickCol
                            && piece.row == clickRow) {
                        // Bring selected piece to top of draw order
                        simPieces.remove(piece);
                        simPieces.add(piece);
                        activeP = piece;
                        // Cache legal moves for hint overlay (piece is at preCol/preRow here)
                        legalMovesGrid = GameState.generateLegalMoves(activeP, simPieces);
                        break;
                    }
                }
            } else {
                simulate();
            }
        }

        // MOUSE RELEASE
        if (!mouse.pressed && activeP != null) {
            int fromCol = activeP.preCol;
            int fromRow = activeP.preRow;
            int targetCol = activeP.col;
            int targetRow = activeP.row;

            if (isMoveValid(targetCol, targetRow)) {
                // Take snapshot BEFORE any modifications (enables undo)
                ArrayList<Piece> snapshot = new ArrayList<>(pieces);

                Move.MoveType moveType = Move.MoveType.NORMAL;

                // Standard capture
                Piece captured = getPieceAt(targetCol, targetRow, activeP);
                if (captured != null) {
                    simPieces.remove(captured);
                    trackCapture(captured);
                    moveType = Move.MoveType.CAPTURE;
                }

                // En passant: pawn moved diagonally onto an empty square
                if (activeP instanceof Pawn && targetCol != fromCol && captured == null) {
                    Piece epPawn = getPieceAt(targetCol, fromRow);
                    if (epPawn != null) {
                        simPieces.remove(epPawn);
                        trackCapture(epPawn);
                        captured = epPawn;
                        moveType = Move.MoveType.EN_PASSANT;
                    }
                }

                // Castling: king moved 2 columns
                if (activeP instanceof King && Math.abs(targetCol - fromCol) == 2) {
                    performCastling(targetCol, fromRow);
                    moveType = Move.MoveType.CASTLING;
                }

                // Commit the move
                activeP.preCol = targetCol;
                activeP.preRow = targetRow;
                activeP.x = activeP.getPosX();
                activeP.y = activeP.getPosY();
                activeP.moveCount++;

                // Record move history with snapshot
                Move move = new Move(fromCol, fromRow, targetCol, targetRow,
                        activeP, captured, moveType, snapshot);
                moveHistory.add(move);
                lastMove = move;

                copyPieces(simPieces, pieces);

                // Pawn promotion must happen before game-over check
                checkPawnPromotion();

                switchTurn();
                checkGameOver();
                // Record algebraic notation (after switchTurn so currentColor = opponent)
                lastMove.notation = AlgebraicNotation.toAlgebraic(lastMove, pieces, currentColor);
                // Play sound feedback
                if (gameOver)                                         sounds.play(SoundManager.SoundType.GAME_OVER);
                else if (GameState.isKingUnderAttack(currentColor, pieces)) sounds.play(SoundManager.SoundType.CHECK);
                else if (lastMove.capturedPiece != null)               sounds.play(SoundManager.SoundType.CAPTURE);
                else                                                   sounds.play(SoundManager.SoundType.MOVE);
            } else {
                // Invalid move — reset to origin
                activeP.col = activeP.preCol;
                activeP.row = activeP.preRow;
                activeP.x = activeP.getPosX();
                activeP.y = activeP.getPosY();
                copyPieces(pieces, simPieces);
            }

            activeP = null;
            legalMovesGrid = null;
        }
    }

    private void simulate() {
        activeP.x = mouse.x - tileSize / 2;
        activeP.y = mouse.y - tileSize / 2;
        // getCol/getRow give screen-space coords; convert to board coords if flipped
        int screenCol = activeP.getCol();
        int screenRow = activeP.getRow();
        activeP.col = boardFlipped ? 7 - screenCol : screenCol;
        activeP.row = boardFlipped ? 7 - screenRow : screenRow;
    }

    private Piece getPieceAt(int col, int row) {
        for (Piece p : simPieces) {
            if (p.col == col && p.row == row) {
                return p;
            }
        }
        return null;
    }

    // Excludes a specific piece — used so the moving piece doesn't match itself
    private Piece getPieceAt(int col, int row, Piece exclude) {
        for (Piece p : simPieces) {
            if (p != exclude && p.col == col && p.row == row) {
                return p;
            }
        }
        return null;
    }

    private boolean isMoveValid(int targetCol, int targetRow) {
        // Must actually move
        if (targetCol == activeP.preCol && targetRow == activeP.preRow) return false;
        // Must stay on board
        if (targetCol < 0 || targetCol > 7 || targetRow < 0 || targetRow > 7) return false;
        // Cannot land on a friendly piece (exclude activeP itself — its col/row tracks the hover square)
        Piece target = getPieceAt(targetCol, targetRow, activeP);
        if (target != null && target.color == activeP.color) return false;

        // Restore piece to origin for all GameState computations
        activeP.col = activeP.preCol;
        activeP.row = activeP.preRow;

        // Castling extra checks: can't castle while in check or through an attacked square
        if (activeP instanceof King && Math.abs(targetCol - activeP.preCol) == 2) {
            if (GameState.isKingUnderAttack(activeP.color, simPieces)) {
                activeP.col = targetCol;
                activeP.row = targetRow;
                return false;
            }
            int midCol = (targetCol + activeP.preCol) / 2; // col 5 (kingside) or col 3 (queenside)
            if (GameState.isSquareAttacked(midCol, activeP.preRow, activeP.color, simPieces)) {
                activeP.col = targetCol;
                activeP.row = targetRow;
                return false;
            }
        }

        boolean valid = GameState.generateLegalMoves(activeP, simPieces)[targetCol][targetRow];
        // Restore to target position for rendering
        activeP.col = targetCol;
        activeP.row = targetRow;
        return valid;
    }

    /** Moves the rook to its post-castling position when the king castles. */
    private void performCastling(int kingTargetCol, int kingRow) {
        boolean kingside = (kingTargetCol == 6);
        int rookFromCol = kingside ? 7 : 0;
        int rookToCol   = kingside ? 5 : 3;

        Piece rook = getPieceAt(rookFromCol, kingRow);
        if (rook instanceof Rook) {
            rook.col = rookToCol;
            rook.preCol = rookToCol;
            rook.x = rook.getPosX();
            rook.y = rook.getPosY();
            rook.moveCount++;
        }
    }

    /** After a move is committed, check if any pawn has reached the promotion row. */
    private void checkPawnPromotion() {
        for (int i = 0; i < simPieces.size(); i++) {
            Piece p = simPieces.get(i);
            if (p instanceof Pawn) {
                boolean promoted = (p.color == Color.WHITE && p.row == 0)
                        || (p.color == Color.BLACK && p.row == 7);
                if (promoted) {
                    promotePawn((Pawn) p, i);
                    return;
                }
            }
        }
    }

    private void promotePawn(Pawn pawn, int index) {
        final Piece[] result = {null};
        try {
            SwingUtilities.invokeAndWait(() -> {
                PromotionDialog dialog = new PromotionDialog(pawn.color);
                dialog.setVisible(true);
                result[0] = dialog.getSelectedPiece(pawn.col, pawn.row, pawn.color);
            });
        } catch (Exception e) {
            result[0] = new Queen(pawn.col, pawn.row, pawn.color); // fallback
        }
        simPieces.set(index, result[0]);
        copyPieces(simPieces, pieces);
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

    private void trackCapture(Piece captured) {
        if (captured.color == Color.BLACK) {
            capturedByWhite.add(captured);
        } else {
            capturedByBlack.add(captured);
        }
    }

    public void undoLastMove() {
        if (moveHistory.isEmpty()) return;

        Move last = moveHistory.remove(moveHistory.size() - 1);
        if (last.pieceList == null || last.pieceStates == null) return;

        // Restore piece list to pre-move state
        pieces.clear();
        pieces.addAll(last.pieceList);

        // Restore each piece's mutable state
        for (Move.PieceState ps : last.pieceStates) {
            ps.restore();
        }

        copyPieces(pieces, simPieces);

        // Restore whose turn it was
        currentColor = last.movedPiece.color;

        // Restore lastMove pointer
        lastMove = moveHistory.isEmpty() ? null : moveHistory.get(moveHistory.size() - 1);

        // Rebuild captured lists from updated history
        rebuildCapturedLists();

        // Clear any game-over state
        gameOver = false;
        gameResult = "";

        // Clear active piece and hints
        activeP = null;
        legalMovesGrid = null;
    }

    private void rebuildCapturedLists() {
        capturedByWhite.clear();
        capturedByBlack.clear();
        for (Move m : moveHistory) {
            if (m.capturedPiece != null) {
                trackCapture(m.capturedPiece);
            }
        }
    }

    public void resetGame() {
        pieces.clear();
        simPieces.clear();
        moveHistory.clear();
        capturedByWhite.clear();
        capturedByBlack.clear();
        activeP = null;
        legalMovesGrid = null;
        lastMove = null;
        currentColor = Color.WHITE;
        gameOver = false;
        gameResult = "";

        createInitialSetup();
        copyPieces(pieces, simPieces);
    }

    /** Exports the current game to a .pgn file chosen by the user. */
    public void exportPgn() {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setSelectedFile(new java.io.File("game.pgn"));
        int result = chooser.showSaveDialog(this);
        if (result != javax.swing.JFileChooser.APPROVE_OPTION) return;

        java.io.File file = chooser.getSelectedFile();
        try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
            writer.println("[Event \"AI Chess Engine Game\"]");
            writer.println("[Date \"" + java.time.LocalDate.now() + "\"]");
            writer.println("[White \"Player 1\"]");
            writer.println("[Black \"Player 2\"]");
            writer.println("[Result \"" + pgnResult() + "\"]");
            writer.println();

            StringBuilder moveLine = new StringBuilder();
            for (int i = 0; i < moveHistory.size(); i++) {
                if (i % 2 == 0) {
                    moveLine.append((i / 2 + 1)).append(". ");
                }
                moveLine.append(moveHistory.get(i).notation).append(" ");
                // Line-wrap at ~80 chars
                if (moveLine.length() > 80 && i % 2 == 1) {
                    writer.println(moveLine.toString().stripTrailing());
                    moveLine.setLength(0);
                }
            }
            if (!moveLine.isEmpty()) writer.println(moveLine.toString().stripTrailing());
            writer.println(pgnResult());
        } catch (java.io.IOException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error saving PGN: " + e.getMessage());
        }
    }

    private String pgnResult() {
        if (!gameOver) return "*";
        if (gameResult.contains("White")) return "1-0";
        if (gameResult.contains("Black")) return "0-1";
        return "1/2-1/2";
    }

    // -------------------------------------------------------------------------
    // DRAWING HELPERS
    // -------------------------------------------------------------------------

    private void drawLastMoveHighlight(Graphics2D g2) {
        if (lastMove == null) return;
        g2.setColor(new java.awt.Color(255, 255, 100, 80));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2.fillRect(scx(lastMove.fromCol), scy(lastMove.fromRow), tileSize, tileSize);
        g2.fillRect(scx(lastMove.toCol),   scy(lastMove.toRow),   tileSize, tileSize);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawLegalMoveHints(Graphics2D g2) {
        if (legalMovesGrid == null) return;
        int dotSize = tileSize / 3;
        int offset  = (tileSize - dotSize) / 2;
        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                if (!legalMovesGrid[col][row]) continue;
                int sx = scx(col);
                int sy = scy(row);
                boolean hasEnemy = getPieceAt(col, row) != null;
                if (hasEnemy) {
                    g2.setColor(new java.awt.Color(50, 220, 50, 160));
                    g2.setStroke(new BasicStroke(5f));
                    g2.drawOval(sx + 6, sy + 6, tileSize - 12, tileSize - 12);
                } else {
                    g2.setColor(new java.awt.Color(50, 220, 50, 160));
                    g2.fillOval(sx + offset, sy + offset, dotSize, dotSize);
                }
            }
        }
        g2.setStroke(new BasicStroke(1f));
    }

    private void drawBoardCoordinates(Graphics2D g2) {
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 13));
        g2.setColor(new java.awt.Color(200, 200, 200));
        for (int i = 0; i < 8; i++) {
            // File letter for this column
            char fileChar = boardFlipped ? (char) ('h' - i) : (char) ('a' + i);
            g2.drawString(String.valueOf(fileChar), i * tileSize + tileSize / 2 - 4, screenHeight - 3);
            // Rank number for this row
            char rankChar = boardFlipped ? (char) ('1' + i) : (char) ('8' - i);
            g2.drawString(String.valueOf(rankChar), 3, i * tileSize + 18);
        }
    }

    private void drawInfoPanel(Graphics2D g2) {
        int panelX = tileSize * 8;
        int panelW = 250;

        // Background
        g2.setColor(new java.awt.Color(30, 30, 30));
        g2.fillRect(panelX, 0, panelW, screenHeight);
        g2.setColor(new java.awt.Color(70, 70, 70));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(panelX, 0, panelW - 1, screenHeight - 1);
        g2.setStroke(new BasicStroke(1f));

        int cx = panelX + 20;
        int y  = 30;

        // --- TURN ---
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        g2.setColor(new java.awt.Color(180, 180, 180));
        g2.drawString("TURN", cx, y);
        y += 28;

        boolean whiteToMove = (currentColor == Color.WHITE);
        java.awt.Color turnColor = whiteToMove
                ? new java.awt.Color(240, 240, 240)
                : new java.awt.Color(100, 100, 255);
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 26));
        g2.setColor(turnColor);
        g2.drawString(whiteToMove ? "WHITE" : "BLACK", cx, y);
        y += 40;

        // --- CHECK STATUS ---
        boolean inCheck = GameState.isKingUnderAttack(currentColor, pieces);
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 15));
        if (gameOver) {
            g2.setColor(new java.awt.Color(255, 200, 50));
            g2.drawString("GAME OVER", cx, y);
        } else if (inCheck) {
            g2.setColor(new java.awt.Color(255, 80, 80));
            g2.drawString("CHECK!", cx, y);
        } else {
            g2.setColor(new java.awt.Color(80, 200, 80));
            g2.drawString("Safe", cx, y);
        }
        y += 40;

        // --- MOVE COUNTER ---
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        g2.setColor(new java.awt.Color(180, 180, 180));
        int fullMoves = (moveHistory.size() / 2) + 1;
        g2.drawString("Move " + fullMoves, cx, y);
        y += 35;

        // Divider
        g2.setColor(new java.awt.Color(70, 70, 70));
        g2.drawLine(cx, y, panelX + panelW - 20, y);
        y += 20;

        // --- CAPTURED PIECES ---
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 13));
        g2.setColor(new java.awt.Color(240, 200, 60));
        g2.drawString("CAPTURED", cx, y);
        y += 22;

        y = drawCapturedRow(g2, cx, y, "White took", capturedByWhite);
        y = drawCapturedRow(g2, cx, y + 8, "Black took", capturedByBlack);

        // --- MOVE HISTORY ---
        y += 12;
        g2.setColor(new java.awt.Color(70, 70, 70));
        g2.drawLine(cx, y, panelX + panelW - 20, y);
        y += 14;

        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 13));
        g2.setColor(new java.awt.Color(240, 200, 60));
        g2.drawString("MOVES", cx, y);
        y += 16;

        g2.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 11));
        int lineH = 15;
        // Show last N move pairs that fit in remaining space (above buttons)
        int maxY = screenHeight - 145;
        int availLines = (maxY - y) / lineH;
        int totalPairs = (moveHistory.size() + 1) / 2;
        int startPair = Math.max(0, totalPairs - availLines);

        for (int pair = startPair; pair < totalPairs && y < maxY; pair++) {
            int moveNum = pair + 1;
            int wi = pair * 2;       // white's move index
            int bi = pair * 2 + 1;   // black's move index
            String wNotation = (wi < moveHistory.size()) ? moveHistory.get(wi).notation : "";
            String bNotation = (bi < moveHistory.size()) ? moveHistory.get(bi).notation : "";

            // Move number
            g2.setColor(new java.awt.Color(120, 120, 120));
            g2.drawString(moveNum + ".", cx, y);
            // White's move
            g2.setColor(new java.awt.Color(220, 220, 220));
            g2.drawString(wNotation, cx + 28, y);
            // Black's move
            g2.setColor(new java.awt.Color(160, 160, 200));
            g2.drawString(bNotation, cx + 100, y);
            y += lineH;
        }
    }

    private int drawCapturedRow(Graphics2D g2, int x, int y,
                                 String label, ArrayList<Piece> captured) {
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        g2.setColor(new java.awt.Color(150, 150, 150));
        g2.drawString(label + " (" + captured.size() + ")", x, y);
        y += 6;

        int iconSize = 28;
        int perRow   = 7;
        for (int i = 0; i < captured.size(); i++) {
            int ix = x + (i % perRow) * (iconSize + 2);
            int iy = y + (i / perRow) * (iconSize + 2);
            Piece p = captured.get(i);
            if (p.image != null) {
                g2.drawImage(p.image, ix, iy, iconSize, iconSize, null);
            }
        }
        int rows = captured.isEmpty() ? 0 : (captured.size() - 1) / perRow + 1;
        return y + rows * (iconSize + 2) + 10;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Board
        boardUI.drawBoard(g2);
        drawLastMoveHighlight(g2);
        drawBoardCoordinates(g2);

        // Non-dragged pieces at their flip-adjusted screen positions
        for (Piece piece : simPieces) {
            if (piece == activeP) continue; // drawn separately below
            boardUI.drawPieceAt(g2, piece, scx(piece.col), scy(piece.row));
        }

        if (activeP != null) {
            // Selection highlight on origin square (flip-aware)
            g2.setColor(java.awt.Color.CYAN);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
            g2.fillRect(scx(activeP.preCol), scy(activeP.preRow), tileSize, tileSize);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            // Legal move dots
            drawLegalMoveHints(g2);

            // Hover target border (flip-aware)
            if (mouse.pressed) {
                g2.setColor(java.awt.Color.WHITE);
                g2.setStroke(new BasicStroke(5f));
                g2.drawRect(scx(activeP.col) + 2, scy(activeP.row) + 3, tileSize - 5, tileSize - 5);
                g2.setStroke(new BasicStroke(1f));
            }

            // Dragged piece follows the mouse (always at p.x, p.y)
            boardUI.drawPiece(g2, activeP);
        }

        // Info panel
        drawInfoPanel(g2);

        // Game over overlay (on top of everything)
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
        String sub = "Press Ctrl+N or 'New Game' to play again";
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18));
        fm = g2.getFontMetrics();
        x = (tileSize * 8 - fm.stringWidth(sub)) / 2;
        g2.setColor(java.awt.Color.LIGHT_GRAY);
        g2.drawString(sub, x, y + 44);
    }
}
