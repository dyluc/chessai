package com.dylanwalsh.chessai.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dylanwalsh.chessai.database.DBConnection;
/**
 * The class of the game that deals with the board and hud. This is the main screen
 of the game. It implements the Screen interface
 * from the LibGDX libraries.
 */
public class GameScreen implements Screen {
    public static final float VIEWPORT_WIDTH = 370;
    private Board board;
    private SpriteBatch sb;
    private OrthographicCamera gameCam;
    private HUD hud;
    private InputMultiplexer input;
    private boolean changingGame = false;
    private String moveHistory = ""; //Move history for new loaded game.
    public GameScreen() {
        sb = new SpriteBatch();
        gameCam = new OrthographicCamera();
        DBConnection.initialize();
        hud = new HUD(this);
        board = new Board(sb, gameCam, hud);
        input = new InputMultiplexer();
        input.addProcessor(hud.getInput());
        input.addProcessor(board.getInput());
        Gdx.input.setInputProcessor(input);
    }
    public void setGame(String moveHistory) {
        changingGame = true;
        this.moveHistory = moveHistory;
    }
    @Override
    public void show() {}
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( .25f, .25f, .25f, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
        sb.setProjectionMatrix(gameCam.combined);
        sb.begin();
        if(!changingGame) {
            board.renderBoard();
        } else { //If the game is being changed, stop calling renderBoard() or Board class.
            board.dispose();
            input.removeProcessor(1);
            board = new Board(sb, gameCam, hud);
            input.addProcessor(board.getInput());
            board.setGame(moveHistory);
            changingGame = false;
            moveHistory = "";
        }
        sb.end();
        hud.renderHUD();
    }
    @Override
    public void resize(int width, int height) {
        //Sets gameCam to orthographic projection centered at 10px right of the right edge of the board, height/2.
        gameCam.setToOrtho(false, VIEWPORT_WIDTH,
                (float)height*(VIEWPORT_WIDTH/(float)width));
        gameCam.position.set(160+10, 160/2, 0);
        gameCam.update();
    }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {
        dispose();
    }
    @Override
    public void dispose() {
        sb.dispose();
        board.dispose();
        hud.dispose();
    }
}
