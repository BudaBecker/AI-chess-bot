package chess;

import java.util.ArrayList;

/**
 * Immutable value object representing a single chess move.
 * Stores a PieceState snapshot of every piece BEFORE the move so any move can
 * be undone.
 */
public class Move {

    public enum MoveType {
        NORMAL, CAPTURE, EN_PASSANT, CASTLING, PROMOTION
    }

    /**
     * Captures the full mutable state of a piece at a moment in time.
     * Call restore() to roll the piece back to that state.
     */
    public static class PieceState {
        public final Piece piece;
        private final int col, row, preCol, preRow, moveCount;

        public PieceState(Piece p) {
            this.piece = p;
            this.col = p.col;
            this.row = p.row;
            this.preCol = p.preCol;
            this.preRow = p.preRow;
            this.moveCount = p.moveCount;
        }

        public void restore() {
            piece.col = col;
            piece.row = row;
            piece.preCol = preCol;
            piece.preRow = preRow;
            piece.moveCount = moveCount;
            piece.x = piece.getPosX();
            piece.y = piece.getPosY();
        }
    }

    public final int fromCol, fromRow;
    public final int toCol, toRow;
    public final Piece movedPiece;
    public final Piece capturedPiece; // null if none
    public final MoveType moveType;

    // Algebraic notation string, set after the move is committed
    public String notation = "";

    // Full board snapshot BEFORE this move (which pieces + their states)
    public final ArrayList<Piece> pieceList;
    public final ArrayList<PieceState> pieceStates;

    public Move(int fromCol, int fromRow, int toCol, int toRow,
            Piece movedPiece, Piece capturedPiece, MoveType moveType,
            ArrayList<Piece> pieces) {
        this.fromCol = fromCol;
        this.fromRow = fromRow;
        this.toCol = toCol;
        this.toRow = toRow;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.moveType = moveType;

        if (pieces != null) {
            this.pieceList = new ArrayList<>(pieces);
            this.pieceStates = new ArrayList<>();
            for (Piece p : pieces)
                pieceStates.add(new PieceState(p));
        } else {
            this.pieceList = null;
            this.pieceStates = null;
        }
    }

    public boolean isTwoSquarePawnAdvance() {
        return moveType == MoveType.NORMAL && movedPiece instanceof chess.pieces.Pawn
                && Math.abs(toRow - fromRow) == 2;
    }

    public boolean isCastling() {
        return moveType == MoveType.CASTLING;
    }
}
