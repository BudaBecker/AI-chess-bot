package chess.pieces;

import java.util.ArrayList;

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

    @Override
    public boolean[][] possibleMoves(ArrayList<Piece> pieces) {
        boolean[][] moves = new boolean[8][8];
        int[][] directions = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };

        for (int[] dir : directions) {
            int c = col + dir[0];
            int r = row + dir[1];
            while (c >= 0 && c < 8 && r >= 0 && r < 8) {
                Piece blocker = getPieceAt(c, r, pieces);
                if (blocker == null) {
                    moves[c][r] = true;
                } else if (blocker.color != color) {
                    moves[c][r] = true; // capture
                    break;
                } else {
                    break; // friendly blocks
                }
                c += dir[0];
                r += dir[1];
            }
        }

        return moves;
    }
}
