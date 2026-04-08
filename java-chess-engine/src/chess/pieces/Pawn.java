package chess.pieces;

import java.util.ArrayList;

import chess.Color;
import chess.Move;
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

    @Override
    public boolean[][] possibleMoves(ArrayList<Piece> pieces) {
        boolean[][] moves = new boolean[8][8];
        // WHITE moves up (row decreases), BLACK moves down (row increases)
        int dir = (color == Color.WHITE) ? -1 : 1;
        int nextRow = row + dir;

        // Forward 1
        if (nextRow >= 0 && nextRow < 8 && getPieceAt(col, nextRow, pieces) == null) {
            moves[col][nextRow] = true;

            // Forward 2 on first move
            if (moveCount == 0) {
                int twoAhead = row + 2 * dir;
                if (twoAhead >= 0 && twoAhead < 8 && getPieceAt(col, twoAhead, pieces) == null) {
                    moves[col][twoAhead] = true;
                }
            }
        }

        // Diagonal captures
        for (int dc : new int[]{-1, 1}) {
            int captureCol = col + dc;
            if (captureCol >= 0 && captureCol < 8 && nextRow >= 0 && nextRow < 8) {
                Piece target = getPieceAt(captureCol, nextRow, pieces);
                if (target != null && target.color != color) {
                    moves[captureCol][nextRow] = true;
                }
            }
        }

        // En passant — check if the last move was a 2-square pawn advance by an adjacent enemy
        Move lastMove = UI.ChessPanel.lastMove;
        if (lastMove != null && lastMove.isTwoSquarePawnAdvance()) {
            // The enemy pawn landed on the same row as us and is adjacent
            if (lastMove.toRow == row && Math.abs(lastMove.toCol - col) == 1) {
                // We capture diagonally to the square the enemy pawn passed through
                moves[lastMove.toCol][row + dir] = true;
            }
        }

        return moves;
    }
}
