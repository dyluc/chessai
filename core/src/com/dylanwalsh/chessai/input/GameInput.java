package com.dylanwalsh.chessai.input;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dylanwalsh.chessai.entities.ChessPiece;
import com.dylanwalsh.chessai.entities.chesspieces.Pawn;
import com.dylanwalsh.chessai.screens.HUD;
import java.util.ArrayList;
/**
 * The class that deals with all game input functionality. It implements the
 InputProcessor interface (InputProcessor is part of
 * LibGDX).
 */
public class GameInput implements InputProcessor {
    private int tileSize;
    private int boardSize;
    private Vector2 refHoverTile;
    private ArrayList<Vector2> refTileSelectionPositions;
    private OrthographicCamera refCam;
    private char promotePawnTo; //Q, R, B, K.
    private ChessPiece[][] refBoard;
    private boolean gameOver;
    private HUD refHud;
    public GameInput(int tileSize, int boardSize, Vector2 refHoverTile,
                     ArrayList<Vector2> refTileSelectionPositions, OrthographicCamera refCam,
                     ChessPiece[][] refBoard, HUD refHud) {
        this.tileSize = tileSize;
        this.boardSize = boardSize;
        this.refHoverTile = refHoverTile;
        this.refTileSelectionPositions = refTileSelectionPositions;
        this.refCam = refCam;
        this.refBoard = refBoard;
        this.refHud = refHud;
        promotePawnTo = 'Q'; //Assume promotion to queen.
        gameOver = false;
    }
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }
    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.Q) {
            if(promotePawnTo != 'Q') {
                promotePawnTo = 'Q';
                refHud.addGameMessage("Next pawn promotion: Queen");
            }
        }
        else if(keycode == Input.Keys.R) {
            if(promotePawnTo != 'R') {
                promotePawnTo = 'R';
                refHud.addGameMessage("Next pawn promotion: Rook");
            }
        }
        else if(keycode == Input.Keys.B) {
            if(promotePawnTo != 'B') {
                promotePawnTo = 'B';
                refHud.addGameMessage("Next pawn promotion: Bishop");
            }
        }
        else if(keycode == Input.Keys.K) {
            if(promotePawnTo != 'K') {
                promotePawnTo = 'K';
                refHud.addGameMessage("Next pawn promotion: Knight");
            }
        }
        return true;
    }
    public char promotePawn(Pawn pawn, char promotePawn) {
        if(promotePawn!='-')
            promotePawnTo = promotePawn;
        return pawn.promote(refBoard, promotePawnTo);
    }
    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(!gameOver) {
            Vector3 worldMouseCoords = refCam.unproject(new Vector3(screenX,
                    screenY, 0));
            if(worldMouseCoords.x >= 0 && worldMouseCoords.x <= tileSize*boardSize
                    && worldMouseCoords.y >= 0 && worldMouseCoords.y <=
                    tileSize*boardSize) {
                float xCoord = (float) Math.floor((worldMouseCoords.x / (tileSize *
                        boardSize)) * boardSize);
                float yCoord = (float) Math.floor((worldMouseCoords.y / (tileSize *
                        boardSize)) * boardSize);
                //Add the tile coordinates to tileSelectionPositions ArrayList in Board.
                        refTileSelectionPositions.add(new Vector2(xCoord, yCoord));
            }
            return true;
        } else {
            return false;
        }
    }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if(!gameOver) {
            Vector3 worldMouseCoords = refCam.unproject(new Vector3(screenX,
                    screenY, 0));
            if(worldMouseCoords.x >= 0 && worldMouseCoords.x <= tileSize*boardSize
                    && worldMouseCoords.y >= 0 && worldMouseCoords.y <=
                    tileSize*boardSize) {
                refHoverTile.x = (float) Math.floor( ( worldMouseCoords.x /
                        (tileSize*boardSize) ) * boardSize );
                refHoverTile.y = (float) Math.floor( ( worldMouseCoords.y /
                        (tileSize*boardSize) ) * boardSize );
            } else {
                refHoverTile.set(-1, -1);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean scrolled(float amountX, float amountY) { return false; }
    public void setGameOver() {
        gameOver = true;
    }
}