package UI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import chess.Piece;
import chess.pieces.Pawn;

public class BoardUI {

    public enum BoardTheme {
        GRAY, BROWN
    }

    final int MAX_COL = 8;
    final int MAX_ROW = 8;
    final int SQUARE_SIZE = ChessPanel.tileSize;

    private BoardTheme theme = BoardTheme.GRAY;

    // Tile images loaded once (null until BROWN theme is first used)
    private BufferedImage brownLight, brownDark;

    public void setTheme(BoardTheme theme) {
        this.theme = theme;
        if (theme == BoardTheme.BROWN && brownLight == null) {
            brownLight = loadTile("res/board/brown_light_tile.png");
            brownDark = loadTile("res/board/brown_dark_tile.png");
        }
    }

    public BoardTheme getTheme() {
        return theme;
    }

    private BufferedImage loadTile(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            return null;
        }
    }

    public void drawBoard(Graphics2D g2) {
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                int x = col * SQUARE_SIZE;
                int y = row * SQUARE_SIZE;
                boolean light = (row + col) % 2 == 0;

                if (theme == BoardTheme.BROWN && brownLight != null && brownDark != null) {
                    g2.drawImage(light ? brownLight : brownDark, x, y, SQUARE_SIZE, SQUARE_SIZE, null);
                } else {
                    g2.setColor(light ? new Color(170, 170, 170) : new Color(70, 70, 70));
                    g2.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                }
            }
        }
    }

    public void drawPiece(Graphics2D g2, Piece p) {
        drawPieceAt(g2, p, p.x, p.y);
    }

    /**
     * Draw a piece at a specific screen position (used for flip-board rendering).
     */
    public void drawPieceAt(Graphics2D g2, Piece p, int x, int y) {
        if (p instanceof Pawn) {
            int offset = (int) (SQUARE_SIZE * 0.1);
            int pieceSize = (int) (SQUARE_SIZE * 0.8);
            g2.drawImage(p.image, x + offset, y + offset, pieceSize, pieceSize, null);
        } else {
            g2.drawImage(p.image, x, y, SQUARE_SIZE, SQUARE_SIZE, null);
        }
    }

    /**
     * Draw a piece image at an arbitrary position and size (used by the info
     * panel).
     */
    public void drawPieceScaled(Graphics2D g2, Piece p, int x, int y, int size) {
        if (p.image != null) {
            g2.drawImage(p.image, x, y, size, size, null);
        }
    }
}
