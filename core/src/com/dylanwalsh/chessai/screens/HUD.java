package com.dylanwalsh.chessai.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Queue;
import com.dylanwalsh.chessai.database.DBConnection;
import com.dylanwalsh.chessai.database.GameData;
import com.dylanwalsh.chessai.entities.ChessPiece;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
/**
 * This class defines the on screen HUD. It is made up of table, text field, text
 button and labels widgets. It implements the
 * Disposable interface so that the Stage, as well as widgets on the stage, and the
 skin used to style the widgets can be disposed.
 */
public class HUD implements Disposable{
    private Stage stage;
    private Skin skin;
    private boolean gameOver;
    private Table table;
    private Table pieceTable;
    private TextField usernameField;
    private TextButton recordGameButton;
    private Table pastGamesTable;
    private TextButton loadGameButton;
    private Label timePlayedLabel;
    private Label movesLabel;
    private Label gameMessage; //Latest game message.
    //This is the game hud's messageQueue. It is an implementation of a queue that stored string items. The addMessage() method
    //will add a new String to the message queue to be displayed on screen.
    private Queue<String> messageQueue;
    private boolean playerTurn;
    private int time;
    private int moves;
    private int win; //Stores whether player won, lost of hasn't finished.
    private String gameMoves; //A string of game moves, this is just a long sequence of numbers stored as TEXT in the database.
    private GameData loadedGame = null;
    public HUD(final GameScreen gameScreen) {
        stage = new Stage();
        stage.getRoot().addCaptureListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int
                    pointer, int button) {
                if (!(event.getTarget() instanceof TextField))
                    stage.setKeyboardFocus(null);
                return false;
            }
        });
        skin = new Skin(Gdx.files.internal("hud/uiskin.json"));
        gameOver = false;
        table = new Table();
        table.setWidth((Gdx.graphics.getWidth()/2f)-80);
        table.setPosition((Gdx.graphics.getWidth()/2f)+40,
                (Gdx.graphics.getHeight()/4f)*2.2f);
        timePlayedLabel = new Label("", skin, "default");
        movesLabel = new Label("Moves: 0", skin, "default");
        gameMessage = new Label("", skin, "default");
        messageQueue = new Queue<String>();
        pieceTable = new Table();
        usernameField = new TextField("", skin, "default");
        usernameField.setMessageText("Enter a Username!");
        //Set gameMoves to empty string in case a save before first move.
        gameMoves = "";
        recordGameButton = new TextButton("Record Game", skin, "default");
        //Definition of new anonymous inner class overriding clicked method to deal with record game button being clicked.
        recordGameButton.addListener(new ClickListener() {
            private boolean clickedUsername = false;
            private boolean clickedLength = false;
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Save game data to database.
                //Only save brand new games or old games unfinished.
                if(loadedGame==null || loadedGame.getWin()==2) {
                    if(loadedGame==null) {//brand new game
                        if(usernameField.getText().isEmpty()) {
                            if(!clickedUsername) {
                                addGameMessage("Enter a Username!");
                                clickedUsername = true;
                            }
                            return; //Make shore there is a username present.
                        }
                        if(usernameField.getText().length() > 20) {
                            if(!clickedLength) {
                                addGameMessage("Max Username Length is 20!");
                                clickedLength = true;
                            }
                            return; //Make shore the length of username is less than or equal to 20.
                        }
                    }
//If updating a game it wont matter what usernameField is.
                    GameData newlySavedGame =
                            DBConnection.saveGame(usernameField.getText(),
                                    time, moves, gameMoves, gameOver?win:2,
                                    loadedGame==null?-1:loadedGame.getGameId());
                    addGameMessage(loadedGame==null?"Game Saved!":"Game Updated!");
                    //Click load game.
                    InputEvent buttonDown = new InputEvent();
                    buttonDown.setType(InputEvent.Type.touchDown);
                    InputEvent buttonUp = new InputEvent();
                    buttonUp.setType(InputEvent.Type.touchUp);
                    loadGameButton.fire(buttonDown);
                    loadGameButton.fire(buttonUp);
                    if(loadedGame == null)
                        loadedGame = newlySavedGame;
                    usernameField.setText("");
                    usernameField.setDisabled(true);
                }
            }
        });
        pastGamesTable = new Table();
        pastGamesTable.setFillParent(true);
        pastGamesTable.bottom();
        final ScrollPane scrollPane = new ScrollPane(pastGamesTable, skin,
                "default");
        scrollPane.setHeight(65);
        scrollPane.setWidth((Gdx.graphics.getWidth()/2f)-80);
        scrollPane.setPosition((Gdx.graphics.getWidth()/2f)+40,
                Gdx.graphics.getHeight()/14f);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setVisible(false);
        loadGameButton = new TextButton("Load Game", skin, "default");
        //Definition of new anonymous inner class overriding clicked method to deal with load game button being clicked.
        loadGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ArrayList<GameData> games = DBConnection.getPastGames();
                pastGamesTable.clearChildren();
                scrollPane.setVisible(true);
                //name : timestamp : score : win/loss/unfinished
                for(GameData g : games) {
                    final GameData game = g; //Declared final so that click listener inner class can access.
                    String gameString =
                            game.getName() + " : " +
                                    game.getTimeStamp().toString() + " : score -> " +
                                    game.getScore() + " : " +

                                    (game.getWin()==0?"Loss":(game.getWin()==1?"Win":"Unfinished"));
                    Label l = new Label(gameString, skin, "default");
                    l.setFontScale(.9f, .9f);
                    pastGamesTable.add(l).expandX();
                    if( !(games.indexOf(game) == (games.size()-1)) )
                        pastGamesTable.row();
                    //Definition of new inner class overriding clicked method to deal with GameData instance button being clicked.
                            l.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            loadedGame = game;
                            pieceTable.clearChildren();
                            time = loadedGame.getTimeSurvived();
                            String s = Integer.toString(time);
                            s = s.replaceAll("\\B(?=(?:..)+$)", ":");
                            timePlayedLabel.setText("Time: "+s);
                            messageQueue.clear();
                            moves = 0;
                            usernameField.setText("");
                            usernameField.setDisabled(true);
                            gameOver = game.getWin()==2?false:true;
                            //Load the game.
                            gameScreen.setGame(loadedGame.getMoveHistory());
                        }
                    });
                }
            }
        });
        table.add(timePlayedLabel).expandX().padBottom(5);
        table.row();
        table.add(movesLabel).expandX().padBottom(20);
        table.row();
        table.add(gameMessage).expandX().padBottom(20);
        table.row();
        table.add(pieceTable).expandX().padBottom(20);
        table.row();
        table.add(usernameField).expandX().padBottom(5);
        table.row();
        table.add(recordGameButton).expandX().padBottom(5);
        table.row();
        table.add(loadGameButton).expandX();
        stage.addActor(table);
        stage.addActor(scrollPane);
        playerTurn = true;
        time = 0;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(playerTurn && !gameOver) {
                    if(time % 100 == 59) {time += 41;}
                    else {time++;}
                    String s = Integer.toString(time);
                    s = s.replaceAll("\\B(?=(?:..)+$)", ":");
                    timePlayedLabel.setText("Time: "+s);
                }
            }
        }, 0, 1000);
    }
    public void changeTurn() {
        playerTurn = !playerTurn;
        //If false, player handed to AI, therefore player made a move.
        if(playerTurn == false) {
            moves++;
            movesLabel.setText("Moves: " + Integer.toString(moves));
        }
    }
    public void setGameOver(int win) {
        gameOver = true;
        this.win = win;
    }
    public void setGameMoves(String gameMoves) {
        this.gameMoves = gameMoves;
    }
    public void addTakenPiece(ChessPiece piece) {
        Texture takenPieceTexture;
        switch(piece.getPiece()) {
            case BBISHOP:
                takenPieceTexture = new Texture("chess_pieces/bbishop.png");
                break;
            case WBISHOP:
                takenPieceTexture = new Texture("chess_pieces/wbishop.png");
                break;
            case BKING:
                takenPieceTexture = new Texture("chess_pieces/bking.png");
                break;
            case WKING:
                takenPieceTexture = new Texture("chess_pieces/wking.png");
                break;
            case BKNIGHT:
                takenPieceTexture = new Texture("chess_pieces/bknight.png");
                break;
            case WKNIGHT:
                takenPieceTexture = new Texture("chess_pieces/wknight.png");
                break;
            case BQUEEN:
                takenPieceTexture = new Texture("chess_pieces/bqueen.png");
                break;
            case WQUEEN:
                takenPieceTexture = new Texture("chess_pieces/wqueen.png");
                break;
            case BROOK:
                takenPieceTexture = new Texture("chess_pieces/brook.png");
                break;
            case WROOK:
                takenPieceTexture = new Texture("chess_pieces/wrook.png");
                break;
            case BPAWN:
                takenPieceTexture = new Texture("chess_pieces/bpawn.png");
                break;
            case WPAWN:
            default:
                takenPieceTexture = new Texture("chess_pieces/wpawn.png");
                break;
        }
        Image takenPiece = new Image(takenPieceTexture);
        pieceTable.add(takenPiece).width(40).height(40);
        if((pieceTable.getChildren().size)%8==0) pieceTable.row();
    }
    public void addGameMessage(String message) {
        messageQueue.addLast(message);
        if(messageQueue.size == 4) messageQueue.removeFirst();
        String msg = "";
        for(String s : messageQueue) {
            msg += s+"\n";
        }
        msg = msg.substring(0, msg.length()-1);
        gameMessage.setText(msg);
    }
    public void renderHUD() {
        stage.act();
        stage.draw();
    }
    /**
     * Used for InputMultiplexer in GameScreen.
     * @return The stage, which itself is an InputProcessor.
     */
    public Stage getInput() {
        return stage;
    }
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}