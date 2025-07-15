package chess.pieces;

import chess.Color;
import chess.Piece;

public class Queen extends Piece {

    public Queen(int col, int row, Color color) {
        super(col, row, color);

        if (color == Color.WHITE) {
            image = getImage("w_queen");

        } else {
            image = getImage("b_queen");
        }
    }

}
