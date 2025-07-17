package UI;

import java.awt.Color;
import java.awt.Graphics2D;

import chess.Piece;
import chess.pieces.Pawn;

public class BoardUI {

    final int MAX_COL = 8;
    final int MAX_ROW = 8;
    final int SQUARE_SIZE = ChessPanel.tileSize;

    public void drawBoard(Graphics2D g2) {
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                int x = col * SQUARE_SIZE;
                int y = row * SQUARE_SIZE;
                boolean isLightSquare = (row + col) % 2 == 0;
                g2.setColor(isLightSquare ? new Color(170, 170, 170) : new Color(70, 70, 70));
                g2.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    public void drawPiece(Graphics2D g2, Piece p) {
        if (p instanceof Pawn) {
            // Small pawns
            int offset = (int) (SQUARE_SIZE * 0.1);
            int pieceSize = (int) (SQUARE_SIZE * 0.8);
            g2.drawImage(p.image, p.x + offset, p.y + offset, pieceSize, pieceSize, null);

        } else {
            g2.drawImage(p.image, p.x, p.y, SQUARE_SIZE, SQUARE_SIZE, null);
        }
    }
}
