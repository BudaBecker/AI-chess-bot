package chess.pieces;

import chess.Color;
import chess.Piece;

public class Rook extends Piece {

    public Rook(int col, int row, Color color) {
        super(col, row, color);

        if (color == Color.WHITE) {
            image = getImage("w_rook");

        } else {
            image = getImage("b_rook");
        }
    }

}
