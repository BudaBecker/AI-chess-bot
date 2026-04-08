package chess.pieces;

import java.util.ArrayList;

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

    @Override
    public boolean[][] possibleMoves(ArrayList<Piece> pieces) {
        boolean[][] moves = new boolean[8][8];
        int[][] steps = { { -1, -1 }, { 0, -1 }, { 1, -1 }, { -1, 0 }, { 1, 0 }, { -1, 1 }, { 0, 1 }, { 1, 1 } };

        for (int[] step : steps) {
            int c = col + step[0];
            int r = row + step[1];
            if (c >= 0 && c < 8 && r >= 0 && r < 8) {
                Piece target = getPieceAt(c, r, pieces);
                if (target == null || target.color != color) {
                    moves[c][r] = true;
                }
            }
        }

        return moves;
    }
}
