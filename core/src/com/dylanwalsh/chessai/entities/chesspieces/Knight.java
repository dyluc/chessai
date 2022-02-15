package com.dylanwalsh.chessai.entities.chesspieces;
import com.badlogic.gdx.math.Vector2;
public class Knight extends com.dylanwalsh.chessai.entities.ChessPiece {
    public Knight(Pieces type, int startX, int startY) {
        super(type, startX, startY);
    }

    @Override
    public void generatePositions(com.dylanwalsh.chessai.entities.ChessPiece[][]
                                          board) {
        //Clear availablePositions and tempPos.
        availablePositions.clear();
        tempPos.clear();
        //Add positions to tempPos (excluding current position).
        //Right of knight.
        tempPos.add(new Vector2(getPosition().x + 1, getPosition().y + 2));
        tempPos.add(new Vector2(getPosition().x + 1, getPosition().y - 2));
        tempPos.add(new Vector2(getPosition().x + 2, getPosition().y + 1));
        tempPos.add(new Vector2(getPosition().x + 2, getPosition().y - 1));
        //Left of knight.
        tempPos.add(new Vector2(getPosition().x - 1, getPosition().y + 2));
        tempPos.add(new Vector2(getPosition().x - 1, getPosition().y - 2));
        tempPos.add(new Vector2(getPosition().x - 2, getPosition().y + 1));
        tempPos.add(new Vector2(getPosition().x - 2, getPosition().y - 1));
        //Validate positions.
        validate(board);
    }
}