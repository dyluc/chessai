package com.dylanwalsh.chessai.entities.chesspieces;
import com.badlogic.gdx.math.Vector2;
public class Queen extends com.dylanwalsh.chessai.entities.ChessPiece {
    public Queen(Pieces type, int startX, int startY){
        super(type, startX, startY);
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
        //Up-right diagonal.
        for(int y=(int)getPosition().y+1, x=(int)getPosition().x+1; y<board.length
                && x<board[(int)getPosition().y].length; y++, x++) {
            tempPos.add(new Vector2(x, y));
            if(board[y][x] != null) break; //otherwise next iteration
        }
        //Up-left diagonal.
        for(int y=(int)getPosition().y+1, x=(int)getPosition().x-1; y<board.length
                && x>=0; y++, x--) {
            tempPos.add(new Vector2(x, y));
            if(board[y][x] != null) break; //otherwise next iteration
        }
        //Down-right.
        for(int y=(int)getPosition().y-1, x=(int)getPosition().x+1; y>=0 &&
                x<board[(int)getPosition().y].length; y--, x++) {
            tempPos.add(new Vector2(x, y));
            if(board[y][x] != null) break; //otherwise next iteration
        }
        //Down-left.
        for(int y=(int)getPosition().y-1, x=(int)getPosition().x-1; y>=0 && x>=0;
            y--, x--) {
            tempPos.add(new Vector2(x, y));
            if(board[y][x] != null) break; //otherwise next iteration
        }
        //Validate positions.
        validate(board);
    }
}