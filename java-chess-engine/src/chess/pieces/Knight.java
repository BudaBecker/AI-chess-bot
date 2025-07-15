package chess.pieces;

import chess.Color;
import chess.Piece;

public class Knight extends Piece {

    public Knight(int col, int row, Color color) {
        super(col, row, color);

        if (color == Color.WHITE) {
            image = getImage("w_knight");

        } else {
            image = getImage("b_knight");
        }
    }

}
