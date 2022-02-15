package com.dylanwalsh.chessai.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.dylanwalsh.chessai.ai.GameAI;
import com.dylanwalsh.chessai.entities.ChessPiece;
import com.dylanwalsh.chessai.entities.chesspieces.Bishop;
import com.dylanwalsh.chessai.entities.chesspieces.King;
import com.dylanwalsh.chessai.entities.chesspieces.Knight;
import com.dylanwalsh.chessai.entities.chesspieces.Pawn;
import com.dylanwalsh.chessai.entities.chesspieces.Queen;
import com.dylanwalsh.chessai.entities.chesspieces.Rook;
import com.dylanwalsh.chessai.input.GameInput;
import com.dylanwalsh.chessai.util.PieceMovements;
import java.util.ArrayList;
/**
 * The Board class encapsulates all functionality to do with the chess board. It
 contains all ChessPiece objects. It implements
 * the Disposable interface so that the tiles and pieces can be disposed.
 */
public class Board implements Disposable{
    private static char turnFlag = 'W'; //W - white(player's turn), B - black(AI's turn), E - end of game.
    private boolean gameOver;
    //Game AI.
    private GameAI gameAI;
    //Reference to the kings and rooks on the board.
    private King whiteKing;
    private King blackKing;
    private Rook leftWhiteRook;
    private Rook rightWhiteRook;
    private Rook leftBlackRook;
    private Rook rightBlackRook;
    //Input.
    private GameInput gameInput;
    private Vector2 hoverTile;
    //Holds x, y coordinates of selected tiles - update method checks which pieces are in that tile.
    private ArrayList<Vector2> tileSelectionPositions;
    private SpriteBatch sb;
    private Texture[] tiles;
    private String[] boardColumns;
    private HUD hud;
    //2D board array of ChessPieces.
    //First element of a 2D array is top left, but when it is draw it is * by index of item and LibGDX origin is at bottom left.
    private ChessPiece[][] board;
    private final int tileSize;
    private final int boardSize;
    public Board(SpriteBatch sb, OrthographicCamera refCam, HUD hud) {
        this.hud = hud;
        tileSize = 20;
        boardSize = 8;
        this.sb = sb;
        board = new ChessPiece[boardSize][boardSize]; //Empty values are null.
        //Input.
        tileSelectionPositions = new ArrayList<Vector2>(2); //0 - first selected tile, 1 - second selected tile.
        hoverTile = new Vector2().set(-1, -1);
        gameInput = new GameInput(tileSize, boardSize, hoverTile,
                tileSelectionPositions, refCam, board, hud);
        //Pieces.
        //White pieces.
        for(int i=0; i<8; i++) { //Pawns.
            board[1][i] = new Pawn(ChessPiece.Pieces.WPAWN, i, 1);
        }
        board[0][3] = new Queen(ChessPiece.Pieces.WQUEEN, 3, 0);
        board[0][2] = new Bishop(ChessPiece.Pieces.WBISHOP, 2, 0);
        board[0][5] = new Bishop(ChessPiece.Pieces.WBISHOP, 5, 0);
        board[0][0] = new Rook(ChessPiece.Pieces.WROOK, 0, 0, 'L'); //Bottom left Rook.
                leftWhiteRook = (Rook)board[0][0];
        leftWhiteRook.setCastlingPosition(3, 0);
        board[0][7] = new Rook(ChessPiece.Pieces.WROOK, 7, 0, 'R'); //Bottom right Rook.
                rightWhiteRook = (Rook)board[0][7];
        rightWhiteRook.setCastlingPosition(5, 0);
        board[0][4] = new King(ChessPiece.Pieces.WKING, 4, 0, leftWhiteRook,
                rightWhiteRook); //White king.
        whiteKing = (King)board[0][4];
        whiteKing.setCastlingPositions(2, 0, 6, 0);
        board[0][1] = new Knight(ChessPiece.Pieces.WKNIGHT, 1, 0);
        board[0][6] = new Knight(ChessPiece.Pieces.WKNIGHT, 6, 0);
        //Black pieces.
        for(int i=0; i<8; i++) { //Pawns.
            board[6][i] = new Pawn(ChessPiece.Pieces.BPAWN, i, 6);
        }
        board[7][3] = new Queen(ChessPiece.Pieces.BQUEEN, 3, 7);
        board[7][2] = new Bishop(ChessPiece.Pieces.BBISHOP, 2, 7);
        board[7][5] = new Bishop(ChessPiece.Pieces.BBISHOP, 5, 7);
        board[7][0] = new Rook(ChessPiece.Pieces.BROOK, 0, 7, 'L'); //Top left Rook.
                leftBlackRook = (Rook)board[7][0];
        leftBlackRook.setCastlingPosition(3, 7);
        board[7][7] = new Rook(ChessPiece.Pieces.BROOK, 7, 7, 'R'); //Top right Rook.
                rightBlackRook = (Rook)board[7][7];
        rightBlackRook.setCastlingPosition(5, 7);
        board[7][4] = new King(ChessPiece.Pieces.BKING, 4, 7, leftBlackRook,
                rightBlackRook); //Black king.
        blackKing = (King)board[7][4];
        blackKing.setCastlingPositions(2, 7, 6, 7);
        board[7][1] = new Knight(ChessPiece.Pieces.BKNIGHT, 1, 7);
        board[7][6] = new Knight(ChessPiece.Pieces.BKNIGHT, 6, 7);
        //Initial generation of piece positions.
        for(ChessPiece[] row : board) {
            for(ChessPiece item : row) {
                if(item != null) {
                    item.generatePositions(board);
                }
            }
        }
        tiles = new Texture[5];
        tiles[0] = new Texture(Gdx.files.internal("tiles/2.png")); //Darker.
        tiles[1] = new Texture(Gdx.files.internal("tiles/1.png")); //Lighter.
        tiles[2] = new Texture(Gdx.files.internal("tiles/hover.png")); //Hover.
        tiles[3] = new Texture(Gdx.files.internal("tiles/move.png")); //Move.
        tiles[4] = new Texture(Gdx.files.internal("tiles/3.png")); //Board background.
                boardColumns = new String[] {"A", "B", "C", "D", "E", "F", "G", "H"};
        //Game AI.
        gameAI = new GameAI();
        //Reset moves in move stack.
        PieceMovements.resetMoves();
    }
    /**
     * The main update method of the game.
     * @return a boolean value to indicate whether the game has finished yet or
    not.
     */
    private boolean update() {
        switch(turnFlag) {
            case 'W':
                //Player's turn - check 1st and 2nd item in pieceSelections.
                try {
                    ChessPiece piece1 =
                            board[(int)tileSelectionPositions.get(0).y][(int)tileSelectionPositions.get(0).x];
                    //a piece has been selected. First mouse up.
                    if(piece1 != null && piece1.getPiece().toString().charAt(0) ==
                    'W')
                    checkAndMove(piece1, (int)tileSelectionPositions.get(1).x,
                            (int)tileSelectionPositions.get(1).y,'-');
                    //checkAndMove will only execute on second mouse up.
                    //Clear positions from tileSelectionPositions.
                    if(tileSelectionPositions.size() >= 2) {
                        tileSelectionPositions.clear();
                    }
                } catch(IndexOutOfBoundsException e) {
                    //No tile coordinates have been added to tileSelectionPositions yet - do nothing.
                }
                break;
            case 'B':
                gameAI.calculateBestMove(board);
                checkAndMove(gameAI.getPiece(), (int)gameAI.getMove().x,
                        (int)gameAI.getMove().y, '-');
                break;
        }
        //Check for checkmate.
        if(isInCheckMate('W')) { //Black won
            hud.addGameMessage("Black has Won!");
            hud.setGameOver(0);
            return true;
        }
        if(isInCheckMate('B')) { //White won
            hud.addGameMessage("White has Won!");
            hud.setGameOver(1);
            return true;
        }
        return false;
    }
    /**
     * This method checks whether the given ChessPiece can be moved to the given
     position(x, y) on the board. It calls moveTo on the
     * piece. If it was successful, moveTo returned true, the kings castling
     conditions are set, the game moves so far are handed over
     * to the hud, the turn in changed and the user is prompted about the move made
     by the AI and if they are in check.
     *
     * @param piece the ChessPiece to move
     * @param x the x-coordinate to move to
     * @param y the y-coordinate to move to
     * @param promotePawn the piece to promote the pawn to if this move is part of
    the loaded game
     * @return will return true if move was successful and turnFlag changed
     */
    private boolean checkAndMove(ChessPiece piece, int x, int y, char promotePawn)
    {
        //gameInput used for by PieceMovements for pawn promotion.
        if(piece != null && piece.moveTo(x, y, board, hud, gameInput, promotePawn))
        {//If it can move, it would have.
            //Update board array, at this point, piece would have updated it's position vector.
            //If it is a Rook or King, set it's moved field to true.
            if(piece.getPiece() == ChessPiece.Pieces.WROOK || piece.getPiece() ==
                    ChessPiece.Pieces.BROOK)
                ((Rook)piece).setMoved();
            else if(piece.getPiece() == ChessPiece.Pieces.WKING || piece.getPiece()
                    == ChessPiece.Pieces.BKING)
                ((King)piece).setMoved();
            //Check castling conditions for next turn.
            whiteKing.setCanCastleLeft(checkCastlingCondition(whiteKing,
                    leftWhiteRook, new Vector2(1, 0), new Vector2(2, 0), new Vector2(3, 0)));
            whiteKing.setCanCastleRight(checkCastlingCondition(whiteKing,
                    rightWhiteRook, new Vector2(5, 0), new Vector2(6, 0)));
            blackKing.setCanCastleLeft(checkCastlingCondition(blackKing,
                    leftBlackRook, new Vector2(1, 7), new Vector2(2, 7), new Vector2(3, 7)));
            blackKing.setCanCastleRight(checkCastlingCondition(blackKing,
                    rightBlackRook, new Vector2(5, 7), new Vector2(6, 7)));
            //Turn finished - hand turn over to opponent.
            hud.changeTurn();
            hud.setGameMoves(PieceMovements.getGameMoves()); //update those moves
            if(turnFlag=='B') {
                hud.addGameMessage("AI moved " + piece.getPiece() + " to " +
                        boardColumns[x] + (y+1));
                if(whiteKing.isInCheck(board)) hud.addGameMessage("Your king is in check!");
                turnFlag = 'W';
                return true;
            }
            if(turnFlag=='W') {
                turnFlag='B';
                return true;
            }
        }
        //If not, nothing happens.
        return false;
    }
    /**
     * Runs through the moveHistory string and moves all pieces on the board
     accordingly.
     * @param moveHistory the string of moves for a game.
     */
    public void setGame(String moveHistory) {
        char[] charArr = moveHistory.toCharArray();
        String nextMove = "";
        for(int i = 0; i < charArr.length; i++) {
            nextMove += charArr[i];
            if(nextMove.length()==4) {
                int previousX = Character.getNumericValue(nextMove.charAt(0));
                int previousY = Character.getNumericValue(nextMove.charAt(1));
                int newX = Character.getNumericValue(nextMove.charAt(2));
                int newY = Character.getNumericValue(nextMove.charAt(3));
                char promotion = '-';
                try{
                    if(charArr[i+1] == 'Q' || charArr[i+1] == 'R' || charArr[i+1]
                            == 'B' || charArr[i+1] == 'K') {
                        promotion = charArr[i+1];
                        i++;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    //The last move made was not a promotion - do nothing.
                }
                checkAndMove(board[previousY][previousX], newX, newY, promotion);
                nextMove = "";
            }
        }
    }
    /**
     * This method checks whether a castling move can be performed given the King
     and Rook involved and the positions in-between
     * them. In order for this method to return true, the following conditions must
     be met:
     * - The King involved in the move cannot be in check (The Rook involved can
     however be in check before a castling move is made).
     * - Both the King and Rook involved in the move should not have previously
     moved during the game.
     * - There can be no pieces in between the Rook and King involved in the
     castling move.
     * - The positions/tiles between the Rook and King cannot be checked by any
     opponent piece (i.e. You cannot castle through checked positions).
     * - The King cannot move into a checked position as part of the castling move.
     */
    private boolean checkCastlingCondition(King king, Rook rook, Vector2...
            inBetweenPositions) { //returns whether or not this you can castle here
        //check if rook is alive
        if(board[(int)rook.getPosition().y][(int)rook.getPosition().x] == null)
            return false;
        ChessPiece b1 =
                board[(int)inBetweenPositions[0].y][(int)inBetweenPositions[0].x];
        ChessPiece c1 =
                board[(int)inBetweenPositions[1].y][(int)inBetweenPositions[1].x];
        ChessPiece d1 = null;
        try {
            d1 = board[(int)inBetweenPositions[2].y][(int)inBetweenPositions[2].x];
        } catch(IndexOutOfBoundsException e) {}
        if(!king.isInCheck(board)) { //King isn't in check.
            if(!king.hasMoved() && !rook.hasMoved() ) { //King and Rook haven't moved.
                if(b1 == null && c1 == null && d1 == null) { //Spaces between rook and king are empty, if there are only two spaces, d1 will still be null.
                    for (ChessPiece[] row : board){
                        for (ChessPiece piece : row) {
                            if (piece != null &&
                                    piece.getPiece().toString().charAt(0) != king.getPiece().toString().charAt(0)) {
//Opposite colour to King involved.
                                try {

                                    if(piece.getAllPotentialMoves().contains(inBetweenPositions[2])) {
                                        //Position 3 exists and is being checked.
                                        return false;
                                    }
//Position 3 exists and isn't being checked.
                                } catch(IndexOutOfBoundsException e) {}
                                //Position 3 either exists and isn't being checked OR doesn't exist.
                                if
                                (piece.getAllPotentialMoves().contains(inBetweenPositions[0]) ||

                                        piece.getAllPotentialMoves().contains(inBetweenPositions[1]) ) {
                                    return false; //Cannot castle.
                                }
                            }
                        }
                    }
                    return true; //Can castle.
                }
            }
        }
        return false;//Cannot castle.
    }
    /**
     * The main draw method of the game, renders all tiles and piece textures.
     */
    private void draw() {
        //2D board
        for(int i = 0; i < board.length; i++) {
            for(int k = 0; k < board[i].length; k++) {
                if( (i%2==0 && k%2==0) || (i%2==1 && k%2==1) ) {
                    sb.draw(tiles[0], tileSize*k, tileSize*i, tileSize, tileSize);
//lighter
                } else {
                    sb.draw(tiles[1], tileSize*k, tileSize*i, tileSize, tileSize);
//darker
                }
                //Hover tile.
                if(k == (int)hoverTile.x && i == (int)hoverTile.y ) {
                    sb.draw(tiles[2], tileSize*k, tileSize*i, tileSize, tileSize);
                }
                //Selection tiles.
                for(Vector2 tilePos : tileSelectionPositions) {
                    if(k == (int)tilePos.x && i == (int)tilePos.y) {
                        sb.draw(tiles[3], tileSize*k, tileSize*i, tileSize,
                                tileSize);
                    }
                }
            }
        }
        //Area around board.
        for(int i = 0; i < board.length; i++) { //Rows.
            sb.draw(tiles[4], -tileSize/2, tileSize*i, tileSize/2, tileSize);
            sb.draw(tiles[4], tileSize*8, tileSize*i, tileSize/2, tileSize);
        }
        for(int k = 0; k < board[0].length; k++) { //Columns.
            sb.draw(tiles[4], tileSize*k, -tileSize/2, tileSize, tileSize/2);
            sb.draw(tiles[4], tileSize*k, tileSize*8, tileSize, tileSize/2);
        }
        sb.draw(tiles[4], (-tileSize/2), (-tileSize/2), tileSize/2, tileSize/2);
        sb.draw(tiles[4], (-tileSize/2), tileSize*8, tileSize/2, tileSize/2);
        sb.draw(tiles[4], tileSize*8, tileSize*8, tileSize/2, tileSize/2);
        sb.draw(tiles[4], tileSize*8, (-tileSize/2), tileSize/2, tileSize/2);
        //Pieces.
        for(ChessPiece[] row : board) {
            for(ChessPiece item : row) {
                if(item != null) { //Only convert to pixel positions here (*tile size).
                    sb.draw(item.getPieceTexture(), item.getPosition().x *
                                    tileSize, item.getPosition().y * tileSize,
                            tileSize, tileSize);
                }
            }
        }
    }
    public boolean renderBoard() {
        //Update.
        if(!gameOver){
            gameOver = update();
            if(gameOver) gameInput.setGameOver(); //Stop dealing with input.
        }
        //Draw;
        draw();
        return gameOver;
    }
    private boolean isInCheckMate(char checkFor) {
        //Loop through every piece of opponent and see if all result in friendly king checked.
        for(ChessPiece[] row : board) {
            for(ChessPiece piece : row) {
                if(piece != null &&
                        piece.getPiece().toString().charAt(0)==checkFor) {//Look at every enemy piece.
                    for(Vector2 move : piece.getAllPotentialMoves()) { //Go through all it's moves.
                        PieceMovements.move(board, piece, (int)move.x,
                                (int)move.y);
                        if(!piece.getFriendlyKing(board).isInCheck(board)) { //If any one saves the king.
                                    PieceMovements.undo();
                            return false; //Then it is not a checkmate.
                        }
                        PieceMovements.undo();
                    }
                }
            }
        }
        return true;
    }
    /**
     * Used for InputMultiplexer in GameScreen
     * @return gameInput - the InputProcessor used for this game.
     */
    public GameInput getInput() {
        return gameInput;
    }
    @Override
    public void dispose() {
        for(Texture texture : tiles) {
            texture.dispose();
        }
        for(ChessPiece[] row : board) { //Dispose whatever is left on the board.
            for(ChessPiece item : row) {
                if(item != null)
                    item.dispose();
            }
        }
    }
}