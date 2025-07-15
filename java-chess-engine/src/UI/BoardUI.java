package UI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import chess.Piece;

public class BoardUI {

    final int MAX_COL = 8;
    final int MAX_ROW = 8;
    final int SQUARE_SIZE = ChessPanel.tileSize;
    final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;

    public void drawBoard(Graphics2D g2) {
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                int x = col * SQUARE_SIZE;
                int y = row * SQUARE_SIZE;
                boolean isLightSquare = (row + col) % 2 == 0;
                g2.setColor(isLightSquare ? new Color(200, 200, 200) : new Color(70, 70, 70));
                g2.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    public void drawPieces(Graphics2D g2, ArrayList<Piece> simPieces) {
        for (Piece p : simPieces) {
            g2.drawImage(p.image, p.getPosX(), p.getPosY(), SQUARE_SIZE, SQUARE_SIZE, null);
        }
    }
}
