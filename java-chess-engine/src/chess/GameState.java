package chess;

import java.util.ArrayList;

import chess.pieces.King;

/**
 * Stateless utility class for chess game state evaluation.
 * All methods accept the piece list as a parameter — no internal mutable state.
 */
public class GameState {

    /**
     * Returns true if the king of the given color is currently under attack.
     */
    public static boolean isKingUnderAttack(Color kingColor, ArrayList<Piece> pieces) {
        King king = findKing(kingColor, pieces);
        if (king == null)
            return false;

        for (Piece enemy : pieces) {
            if (enemy.color != kingColor && enemy.possibleMoves(pieces)[king.col][king.row]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an 8x8 grid of legal moves for the given piece.
     * A legal move is a possible move that does not leave the player's king in
     * check.
     */
    public static boolean[][] generateLegalMoves(Piece piece, ArrayList<Piece> pieces) {
        boolean[][] legal = new boolean[8][8];
        boolean[][] possible = piece.possibleMoves(pieces);

        int origCol = piece.col;
        int origRow = piece.row;

        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                if (!possible[col][row])
                    continue;

                // Simulate the move
                Piece captured = removePieceAt(col, row, piece, pieces);
                piece.col = col;
                piece.row = row;

                if (!isKingUnderAttack(piece.color, pieces)) {
                    legal[col][row] = true;
                }

                // Restore
                piece.col = origCol;
                piece.row = origRow;
                if (captured != null) {
                    pieces.add(captured);
                }
            }
        }

        return legal;
    }

    /**
     * Returns true if the given color is in checkmate (in check with no legal
     * moves).
     */
    public static boolean isCheckmate(Color color, ArrayList<Piece> pieces) {
        return isKingUnderAttack(color, pieces) && !hasAnyLegalMove(color, pieces);
    }

    /**
     * Returns true if the given color is in stalemate (not in check but no legal
     * moves).
     */
    public static boolean isStalemate(Color color, ArrayList<Piece> pieces) {
        return !isKingUnderAttack(color, pieces) && !hasAnyLegalMove(color, pieces);
    }

    private static boolean hasAnyLegalMove(Color color, ArrayList<Piece> pieces) {
        for (Piece p : pieces) {
            if (p.color == color) {
                boolean[][] legal = generateLegalMoves(p, pieces);
                for (int c = 0; c < 8; c++) {
                    for (int r = 0; r < 8; r++) {
                        if (legal[c][r])
                            return true;
                    }
                }
            }
        }
        return false;
    }

    private static King findKing(Color color, ArrayList<Piece> pieces) {
        for (Piece p : pieces) {
            if (p instanceof King && p.color == color) {
                return (King) p;
            }
        }
        return null;
    }

    /** Removes and returns an enemy piece at (col, row), or null if none. */
    private static Piece removePieceAt(int col, int row, Piece mover, ArrayList<Piece> pieces) {
        for (int i = 0; i < pieces.size(); i++) {
            Piece p = pieces.get(i);
            if (p != mover && p.col == col && p.row == row) {
                pieces.remove(i);
                return p;
            }
        }
        return null;
    }
}
