package chess.pieces;

import chess.Color;
import chess.Piece;

public class King extends Piece {

    public King(int col, int row, Color color) {
        super(col, row, color);

        if (color == Color.WHITE) {
            image = getImage("w_king");

        } else {
            image = getImage("b_king");
        }
    }

}
