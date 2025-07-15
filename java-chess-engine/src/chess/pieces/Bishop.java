package chess.pieces;

import chess.Color;
import chess.Piece;

public class Bishop extends Piece {

    public Bishop(int col, int row, Color color) {
        super(col, row, color);

        if (color == Color.WHITE) {
            image = getImage("w_bishop");

        } else {
            image = getImage("b_bishop");
        }
    }

}
