package chess;

import java.util.ArrayList;

import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

/**
 * Converts a Move to standard algebraic notation (SAN).
 * Handles normal moves, captures, castling, promotion, check (+) and checkmate (#).
 */
public class AlgebraicNotation {

    /**
     * Converts a move to its SAN string.
     *
     * @param move          the move to convert
     * @param piecesAfter   board state AFTER the move was made
     * @param opponentColor the color whose turn it is after the move
     */
    public static String toAlgebraic(Move move, ArrayList<Piece> piecesAfter, Color opponentColor) {
        // Castling
        if (move.isCastling()) {
            String base = (move.toCol == 6) ? "O-O" : "O-O-O";
            return base + checkSuffix(opponentColor, piecesAfter);
        }

        Piece p = move.movedPiece;
        StringBuilder sb = new StringBuilder();

        boolean isPawn = (p instanceof Pawn);
        boolean isCapture = (move.capturedPiece != null);

        // Piece letter
        if (!isPawn) {
            sb.append(pieceChar(p));
        }

        // Source file disambiguation
        if (isPawn && isCapture) {
            sb.append(fileChar(move.fromCol));
        } else if (!isPawn) {
            // Add disambiguation if needed (same piece type can reach same destination)
            String disambig = disambiguation(move, piecesAfter);
            sb.append(disambig);
        }

        // Capture marker
        if (isCapture) sb.append('x');

        // Destination
        sb.append(fileChar(move.toCol));
        sb.append(rankChar(move.toRow));

        // Promotion
        if (move.moveType == Move.MoveType.PROMOTION) {
            Piece promoted = getPieceAt(move.toCol, move.toRow, piecesAfter);
            if (promoted != null && !(promoted instanceof Pawn)) {
                sb.append('=').append(pieceChar(promoted));
            } else {
                sb.append("=Q"); // fallback
            }
        }

        sb.append(checkSuffix(opponentColor, piecesAfter));
        return sb.toString();
    }

    // -------------------------------------------------------------------------

    private static String checkSuffix(Color opponentColor, ArrayList<Piece> pieces) {
        if (GameState.isCheckmate(opponentColor, pieces)) return "#";
        if (GameState.isKingUnderAttack(opponentColor, pieces)) return "+";
        return "";
    }

    /**
     * Returns a disambiguation string (source file, rank, or both) if multiple
     * pieces of the same type can reach the same square.
     */
    private static String disambiguation(Move move, ArrayList<Piece> piecesAfter) {
        Piece mover = move.movedPiece;
        // Check if any other piece of the same type and color could also reach toCol/toRow
        boolean sameFile = false;
        boolean sameRank = false;
        int ambiguous = 0;

        for (Piece p : piecesAfter) {
            if (p == mover || p.color != mover.color || p.getClass() != mover.getClass()) continue;
            // Does this piece cover the destination in a simplified sense?
            // We check possibleMoves from the current position
            boolean[][] canMove = p.possibleMoves(piecesAfter);
            if (canMove[move.toCol][move.toRow]) {
                ambiguous++;
                if (p.col == mover.col) sameFile = true;
                if (p.row == mover.row) sameRank = true;
            }
        }

        if (ambiguous == 0) return "";
        if (!sameFile) return fileChar(move.fromCol);
        if (!sameRank) return String.valueOf(rankChar(move.fromRow));
        return fileChar(move.fromCol) + rankChar(move.fromRow);
    }

    private static char pieceChar(Piece p) {
        if (p instanceof King)   return 'K';
        if (p instanceof Queen)  return 'Q';
        if (p instanceof Rook)   return 'R';
        if (p instanceof Bishop) return 'B';
        if (p instanceof Knight) return 'N';
        return ' ';
    }

    private static String fileChar(int col) {
        return String.valueOf((char) ('a' + col));
    }

    private static char rankChar(int row) {
        return (char) ('0' + (8 - row));
    }

    private static Piece getPieceAt(int col, int row, ArrayList<Piece> pieces) {
        for (Piece p : pieces) {
            if (p.col == col && p.row == row) return p;
        }
        return null;
    }
}
