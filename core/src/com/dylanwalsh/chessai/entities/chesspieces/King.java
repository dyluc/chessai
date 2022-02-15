package com.dylanwalsh.chessai.entities.chesspieces;
import com.badlogic.gdx.math.Vector2;
public class King extends com.dylanwalsh.chessai.entities.ChessPiece {
    //References to castle conditions in Board so that king can know when to add castle position to its availablePositions.
    private boolean canCastleLeft;
    private boolean canCastleRight;
    private Rook castleLeftRook;
    private Rook castleRightRook;
    private Vector2 castleLeftPosition;
    private Vector2 castleRightPosition;
    //Variable to track whether this King has moved.
    private boolean moved = false;
    public King(Pieces type, int startX, int startY, Rook castleLeftRook, Rook
            castleRightRook) {
        super(type, startX, startY);
        this.castleLeftRook = castleLeftRook;
        this.castleRightRook = castleRightRook;
        canCastleLeft = false;
        canCastleRight = false;
    }
    /**
     * A method that returns true if this king is in check. The method calls
     isCheckingKing() of every ChessPiece
     * on the board until one returns true.
     * @param board The 2D board array.
     * @return true or false, depending on whether this king is currently in check
     */
    public boolean isInCheck(com.dylanwalsh.chessai.entities.ChessPiece[][] board)
    {
        for(com.dylanwalsh.chessai.entities.ChessPiece[] row : board) {
            for(com.dylanwalsh.chessai.entities.ChessPiece piece : row) {
                if(piece != null && piece.getPiece().toString().charAt(0) !=
                        getPiece().toString().charAt(0) && piece.isCheckingKing()) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * This method is only called once during the initialisation of the board. It
     sets the two positions this King will
     * be able to move to if a castling move were to be performed.
     * @param lx the x position if this king were to castle left.
     * @param ly the y position if this king were to castle left.
     * @param rx the x position if this king were to castle right.
     * @param ry the y position if this king were to castle right.
     */
    public void setCastlingPositions(int lx, int ly, int rx, int ry) {
        castleLeftPosition = new Vector2(lx, ly);
        castleRightPosition = new Vector2(rx, ry);
    }
    public boolean canCastleLeft() {
        return canCastleLeft;
    }
    public boolean canCastleRight() {
        return canCastleRight;
    }
    public Rook getCastleLeftRook() {
        return castleLeftRook;
    }
    public Rook getCastleRightRook() {
        return castleRightRook;
    }
    public Vector2 getCastleLeftPosition() {
        return castleLeftPosition;
    }
    public Vector2 getCastleRightPosition() {
        return castleRightPosition;
    }
    public void setCanCastleLeft(boolean canCastleLeft) {
        this.canCastleLeft = canCastleLeft;
    }
    public void setCanCastleRight(boolean canCastleRight) {
        this.canCastleRight = canCastleRight;
    }
    public void setMoved() {
        moved = true;
    }
    public boolean hasMoved() {
        return moved;
    }
    @Override
    public void generatePositions(com.dylanwalsh.chessai.entities.ChessPiece[][]
                                          board) {
        //Clear availablePositions and tempPos.
        availablePositions.clear();
        tempPos.clear();
        //Add positions to tempPos (excluding current position).
        //Left column.
        for(int i=-1; i<2; i++) { tempPos.add(new Vector2(getPosition().x-1,
                getPosition().y+i)); }
        //Right column.
        for(int i=-1; i<2; i++) { tempPos.add(new Vector2(getPosition().x+1,
                getPosition().y+i)); }
        //Top and bottom.
        tempPos.add(new Vector2(getPosition().x, getPosition().y+1));
        tempPos.add(new Vector2(getPosition().x, getPosition().y-1));
        //Add the castling positions if possible.
        if(canCastleLeft) {
            availablePositions.add(new Vector2((int)castleLeftPosition.x,
                    (int)castleLeftPosition.y));
        }
        if(canCastleRight) {
            availablePositions.add(new Vector2((int)castleRightPosition.x,
                    (int)castleRightPosition.y));
        }
        //Validate positions.
        validate(board);
    }
}