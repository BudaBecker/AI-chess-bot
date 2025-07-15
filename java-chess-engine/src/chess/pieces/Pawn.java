package chess.pieces;

import chess.Color;
import chess.Piece;

public class Pawn extends Piece {

    public Pawn(int col, int row, Color color) {
        super(col, row, color);

        if (color == Color.WHITE) {
            image = getImage("w_pawn");

        } else {
            image = getImage("b_pawn");
        }
    }

}
