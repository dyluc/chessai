package com.dylanwalsh.chessai.util;
import com.dylanwalsh.chessai.entities.ChessPiece;
/**
 * This class represents a single piece movement, it keeps track of information
 associated with moving a piece. This includes the
 * piece to move, where to move it to, where it is moving from, if there is a piece
 in the position it is moving to, whether this
 * movement is part of a castling move (which would mean that it is either a rook
 or king).
 */
public class PieceMovement {
    private ChessPiece piece;
    private int fromX;
    private int fromY;
    private int toX;
    private int toY;
    //If this is a rook or king castling.
    private boolean castle;
    //If there is a piece at that position, record it for later use. Will be null if no piece is there.
    private ChessPiece pieceAtPosition;
    public PieceMovement(ChessPiece piece, int toX, int toY, ChessPiece
            pieceAtPosition, boolean castle) {
        this.piece = piece;
        this.fromX = (int)piece.getPosition().x;
        this.fromY = (int)piece.getPosition().y;
        this.toX = toX;
        this.toY = toY;
        this.pieceAtPosition = pieceAtPosition;
        this.castle = castle;
    }
    public boolean isCastle() {
        return castle;
    }
    public ChessPiece getPiece() { return piece; }
    public ChessPiece getPieceAtPosition() { return pieceAtPosition; }
    public int getFromX() { return fromX; }
    public int getFromY() {
        return fromY;
    }
    public int getToX() {
        return toX;
    }
    public int getToY() {
        return toY;
    }
}