package chess.pieces;

import java.util.ArrayList;

import chess.Color;
import chess.GameState;
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
        int[][] steps = {{-1, -1}, {0, -1}, {1, -1}, {-1, 0}, {1, 0}, {-1, 1}, {0, 1}, {1, 1}};

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

        // Castling candidates — attack-path safety is verified in ChessPanel.isMoveValid
        if (moveCount == 0 && col == 4) {
            // Kingside: rook at col 7, squares 5-6 must be empty
            Piece kRook = getPieceAt(7, row, pieces);
            if (kRook instanceof Rook && kRook.color == color && kRook.moveCount == 0
                    && GameState.isPathClear(col, 7, row, pieces)) {
                moves[6][row] = true;
            }
            // Queenside: rook at col 0, squares 1-3 must be empty
            Piece qRook = getPieceAt(0, row, pieces);
            if (qRook instanceof Rook && qRook.color == color && qRook.moveCount == 0
                    && GameState.isPathClear(0, col, row, pieces)) {
                moves[2][row] = true;
            }
        }

        return moves;
    }
}
