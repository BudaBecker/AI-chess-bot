package chess.pieces;

import java.util.ArrayList;

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

    @Override
    public boolean[][] possibleMoves(ArrayList<Piece> pieces) {
        boolean[][] moves = new boolean[8][8];
        int[][] jumps = { { -2, -1 }, { -2, 1 }, { -1, -2 }, { -1, 2 }, { 1, -2 }, { 1, 2 }, { 2, -1 }, { 2, 1 } };

        for (int[] jump : jumps) {
            int c = col + jump[0];
            int r = row + jump[1];
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
