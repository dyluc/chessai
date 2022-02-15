package com.dylanwalsh.chessai.entities.chesspieces;
import com.badlogic.gdx.math.Vector2;
public class Bishop extends com.dylanwalsh.chessai.entities.ChessPiece {
    public Bishop(Pieces type, int startX, int startY){ super(type, startX,
            startY); }
    @Override
    public void generatePositions(com.dylanwalsh.chessai.entities.ChessPiece[][]
                                          board) {
        //Clear availablePositions and tempPos.
        availablePositions.clear();
        tempPos.clear();
        //Add positions to tempPos (excluding current position).
        //Up-right diagonal.
        for(int y=(int)getPosition().y+1, x=(int)getPosition().x+1; y<board.length
                && x<board[(int)getPosition().y].length; y++, x++) {
            tempPos.add(new Vector2(x, y));
            if(board[y][x] != null) break; //Otherwise next iteration.
        }
        //Up-left diagonal.
        for(int y=(int)getPosition().y+1, x=(int)getPosition().x-1; y<board.length
                && x>=0; y++, x--) {
            tempPos.add(new Vector2(x, y));
            if(board[y][x] != null) break; //Otherwise next iteration.
        }
        //Down-right diagonal.
        for(int y=(int)getPosition().y-1, x=(int)getPosition().x+1; y>=0 &&
                x<board[(int)getPosition().y].length; y--, x++) {
            tempPos.add(new Vector2(x, y));
            if(board[y][x] != null) break; //Otherwise next iteration.
        }
        //Down-left diagonal.
        for(int y=(int)getPosition().y-1, x=(int)getPosition().x-1; y>=0 && x>=0;
            y--, x--) {
            tempPos.add(new Vector2(x, y));
            if(board[y][x] != null) break; //Otherwise next iteration.
        }
        //Validate positions.
        validate(board);
    }
}