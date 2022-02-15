package com.dylanwalsh.chessai.entities.chesspieces;
import com.badlogic.gdx.math.Vector2;
public class Pawn extends com.dylanwalsh.chessai.entities.ChessPiece {
    public Pawn(Pieces type, int startX, int startY){
        super(type, startX, startY);
    }
    @Override
    public void generatePositions(com.dylanwalsh.chessai.entities.ChessPiece[][]
                                          board) {
        //Clear availablePositions and tempPos.
        availablePositions.clear();
        tempPos.clear();
        //Add positions to tempPos (excluding current position).
        switch(getPiece()) {
            case BPAWN:
                tempPos.add(new Vector2(getPosition().x, getPosition().y-1));
                //Check position - determine if made first move.
                if(getPosition().y == 6) { //Index 6 - hasn't moved.
                    if(board[(int)getPosition().y-1][(int)getPosition().x] == null)
                        tempPos.add(new Vector2(getPosition().x, getPosition().y-2)); //two down
                }
                break;
            case WPAWN:
                tempPos.add(new Vector2(getPosition().x, getPosition().y+1));
                //Check position - determine if made first move.
                if(getPosition().y == 1) { //Index 1 - hasn't moved
                    if(board[(int)getPosition().y+1][(int)getPosition().x] == null)
                        tempPos.add(new Vector2(getPosition().x,
                                getPosition().y+2)); //two up
                }
                break;
        }
        //Validate positions.
        validate(board);
    }
    public char promote(com.dylanwalsh.chessai.entities.ChessPiece[][] board, char
            promoteTo) {
        //For black, 50% chance of Queen of Knight.
        if(getPiece().toString().charAt(0) == 'B' && promoteTo=='-') {
            char[] p = new char[]{'Q', 'K'};
            promoteTo = p[(int)Math.round(Math.random())];
        }
        switch(promoteTo) {
            case 'Q':
                board[(int)getPosition().y][(int)getPosition().x] = new
                        Queen(getPiece()==Pieces.WPAWN?Pieces.WQUEEN:Pieces.BQUEEN, (int)getPosition().x,
                        (int)getPosition().y);
                dispose(); //Dispose this pawn.
                break;
            case 'K':
                board[(int)getPosition().y][(int)getPosition().x] = new
                        Knight(getPiece()==Pieces.WPAWN?Pieces.WKNIGHT:Pieces.BKNIGHT,
                        (int)getPosition().x, (int)getPosition().y);
                dispose(); //Dispose this pawn.
                break;
            case 'B':
                board[(int)getPosition().y][(int)getPosition().x] = new
                        Bishop(getPiece()==Pieces.WPAWN?Pieces.WBISHOP:Pieces.BBISHOP,
                        (int)getPosition().x, (int)getPosition().y);
                dispose(); //Dispose this pawn.
                break;
            case 'R':
                board[(int)getPosition().y][(int)getPosition().x] = new
                        Rook(getPiece()==Pieces.WPAWN?Pieces.WROOK:Pieces.BROOK, (int)getPosition().x,
                        (int)getPosition().y, 'N');
                dispose(); //Dispose this pawn.
                break;
        }
        //Call generatePositions on newly added piece.
        board[(int)getPosition().y][(int)getPosition().x].generatePositions(board);
        return promoteTo;
    }
}