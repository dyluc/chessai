package com.dylanwalsh.chessai.entities.chesspieces;
import com.badlogic.gdx.math.Vector2;
public class Rook extends com.dylanwalsh.chessai.entities.ChessPiece {
    private Vector2 castlePosition;
    //Left or right rook?
    private char rookType; //Left or right -> 'L', 'R', for pawns promoting to new rook -> 'N' (this could be any value).
    //Variable to track whether this Rook has moved.
    private boolean moved = false;
    public Rook(Pieces type, int startX, int startY, char rookType){
        super(type, startX, startY);
        this.rookType = rookType;
    }
    /**
     * This method is only called once during the initialisation of the board. It
     sets the position this Rook will be able to move
     * to if a castling move were to be performed.
     * @param x the x position if this rook were to castle.
     * @param y the y position if this king were to castle.
     */
    public void setCastlingPosition(int x, int y) {
        castlePosition = new Vector2(x, y);
    }
    public char getType() { return rookType; }
    public void setMoved() {
        moved = true;
    }
    public boolean hasMoved() {
        return moved;
    }
    public Vector2 getCastlePosition() {
        return castlePosition;
    }
    @Override
    public void generatePositions(com.dylanwalsh.chessai.entities.ChessPiece[][]
                                          board) {
        //Clear availablePositions and tempPos.
        availablePositions.clear();
        tempPos.clear();
        //Add positions to tempPos (excluding current position).
        //Up.
        for(int y = (int)getPosition().y+1; y<board.length; y++) {
            tempPos.add(new Vector2(getPosition().x, y));
            if(board[y][(int)getPosition().x] != null) break; //otherwise next iteration
        }
        //Down.
        for(int y = (int)getPosition().y-1; y>=0; y--) {
            tempPos.add(new Vector2(getPosition().x, y));
            if(board[y][(int)getPosition().x] != null) break; //otherwise next iteration
        }
        //Right.
        for(int x = (int)getPosition().x+1; x<board[(int)getPosition().y].length;
            x++) {
            tempPos.add(new Vector2(x, getPosition().y));
            if(board[(int)getPosition().y][x] != null) break; //otherwise next iteration
        }
        //Left.
        for(int x = (int)getPosition().x-1; x>=0; x--) {
            tempPos.add(new Vector2(x, getPosition().y));
            if(board[(int)getPosition().y][x] != null) break; //otherwise next iteration
        }
        //Validate positions.
        validate(board);
    }
}
