package com.dylanwalsh.chessai.entities;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.dylanwalsh.chessai.entities.chesspieces.King;
import com.dylanwalsh.chessai.entities.chesspieces.Pawn;
import com.dylanwalsh.chessai.input.GameInput;
import com.dylanwalsh.chessai.screens.HUD;
import com.dylanwalsh.chessai.util.PieceMovements;
import java.util.ArrayList;
/**
 * This is an abstract class containing all functionality for a chess piece. Piece
 specific functionality is implemented in child
 * classes inheriting from ChessPiece. This class implements Disposable so that it
 can dispose chess piece textures.
 */
public abstract class ChessPiece implements Disposable{
    /**
     * This is a enum storing all different types of pieces. Every child class of
     ChessPiece must call the ChessPiece
     * constructor to initialize the piece value, using one of these enum values.
     */
    public enum Pieces { //Implicitly static.
        WBISHOP, WKING, WKNIGHT, WPAWN, WQUEEN, WROOK,
        BBISHOP, BKING, BKNIGHT, BPAWN, BQUEEN, BROOK
    }
    private final Pieces piece;
    private Vector2 position;
    //Temporary positions added to in generatePositions, validate() method checks if these positions are valid
    //and then adds to availablePositions.
    protected ArrayList<Vector2> tempPos;
    //Positions that this piece can move to. Will not include current position.
    protected ArrayList<Vector2> availablePositions;
    //Contains list of potential takes for this piece.
    private ArrayList<Vector2> potentialTakes;
    private final Texture pieceTexture;
    private boolean checkingKing;
    /**
     * The ChessPiece constructor, a call to super from the child constructor
     initializes the chess piece's type, and start position
     * and assigns the piece the appropriate texture.
     * @param piece the type of piece.
     * @param startX the starting x value of this piece.
     * @param startY the starting y value of this piece.
     */
    public ChessPiece(Pieces piece, int startX, int startY) {
        this.piece = piece;
        position = new Vector2().set(startX, startY);
        tempPos = new ArrayList<Vector2>();
        availablePositions = new ArrayList<Vector2>();
        potentialTakes = new ArrayList<Vector2>();
        checkingKing = false;
        switch(piece) {
            case BBISHOP:
                pieceTexture = new Texture("chess_pieces/bbishop.png");
                break;
            case WBISHOP:
                pieceTexture = new Texture("chess_pieces/wbishop.png");
                break;
            case BKING:
                pieceTexture = new Texture("chess_pieces/bking.png");
                break;
            case WKING:
                pieceTexture = new Texture("chess_pieces/wking.png");
                break;
            case BKNIGHT:
                pieceTexture = new Texture("chess_pieces/bknight.png");
                break;
            case WKNIGHT:
                pieceTexture = new Texture("chess_pieces/wknight.png");
                break;
            case BQUEEN:
                pieceTexture = new Texture("chess_pieces/bqueen.png");
                break;
            case WQUEEN:
                pieceTexture = new Texture("chess_pieces/wqueen.png");
                break;
            case BROOK:
                pieceTexture = new Texture("chess_pieces/brook.png");
                break;
            case WROOK:
                pieceTexture = new Texture("chess_pieces/wrook.png");
                break;
            case BPAWN:
                pieceTexture = new Texture("chess_pieces/bpawn.png");
                break;
            case WPAWN:
            default:
                pieceTexture = new Texture("chess_pieces/wpawn.png");
                break;
        }
    }
    /**
     * @return the piece texture.
     */
    public Texture getPieceTexture() {
        return pieceTexture;
    }
    /**
     *
     * @param posX
     * @param posY
     * @param board
     * @param hud
     * @param in
     * @param promotePawn The piece this pawn will be promoted to if it is known
    (this is a loaded game). Otherwise '-'.
     * @return
     */
    public boolean moveTo(int posX, int posY, ChessPiece[][] board, HUD hud,
                          GameInput in, char promotePawn) {
        //Look over all the available positions for this chess piece. Here I look over each item in the list and compare it to
        //the posX, posY position passed into this method.
        for (Vector2 pos : availablePositions) {
            if (pos.x == posX && pos.y == posY) {
                //Valid move.
                PieceMovements.move(board, this, posX, posY);
                if (getFriendlyKing(board).isInCheck(board)) {
                    PieceMovements.undo();
                    return false;
                }
//Record last game move.
                PieceMovements.recordLastMove();
                //Check if pawn can be promoted.
                switch (getPiece()) {
                    case BPAWN:
                        if (posY == 0) {
                            PieceMovements.promoteBlackPawn(((Pawn) this),
                                    promotePawn);
                        }
                        break;
                    case WPAWN:
                        if (posY == 7) {
                            PieceMovements.promoteWhitePawn(((Pawn) this),
                                    promotePawn, in);
                        }
                        break;
                }
                return true;
            }
        }
        for (Vector2 pos : potentialTakes) {
            if (pos.x == posX && pos.y == posY) {
                //Valid move.
                ChessPiece referenceToPiece = PieceMovements.move(board, this,
                        posX, posY);
                if (getFriendlyKing(board).isInCheck(board)) {
                    PieceMovements.undo();
                    return false;
                }
                //Add taken piece to hud.
                hud.addTakenPiece(referenceToPiece);
                referenceToPiece.dispose(); //Dispose the taken piece.
                //Record last game move.
                PieceMovements.recordLastMove();
                //Check if pawn can be promoted.
                switch (getPiece()) {
                    case BPAWN:
                        if (posY == 0)
                            PieceMovements.promoteBlackPawn(((Pawn) this),
                                    promotePawn);
                        break;
                    case WPAWN:
                        if (posY == 7) {
                            PieceMovements.promoteWhitePawn(((Pawn) this),
                                    promotePawn, in);
                        }
                        break;
                }
                return true;
            }
        }
        return false;
    }
    public King getFriendlyKing(ChessPiece[][] board) {
        for(ChessPiece[] row : board) {
            for(ChessPiece piece : row) {
                if(piece != null)
                    if(piece.getPiece() == Pieces.BKING || piece.getPiece() ==
                            Pieces.WKING) //It is a King.
                if(piece.getPiece().toString().charAt(0) ==
                        getPiece().toString().charAt(0)) {//Of the same colour.
                    return (King) piece;
                }
            }
        }
        return null;
    }
    /**
     * Returns the state of the checkingKing flag.
     *
     * @return true of false, depending on whether this piece is checking the
    opponent's king.
     */
    public boolean isCheckingKing() {
        return checkingKing;
    }
    /**
     * A method called automatically by validate() after every move which sets the
     checkingKing flag to true or false depending
     * on whether the potentialTakes contains the opponent's king.
     */
    private void setCheckingKing(ChessPiece[][] board) {
        //PotentialTakes will only contain opponent pieces, and therefore won't contain this sides king.
        for(Vector2 piece : potentialTakes) {
            if (board[(int) piece.y][(int) piece.x].getPiece() == Pieces.BKING ||
                    board[(int) piece.y][(int) piece.x].getPiece() == Pieces.WKING) {
                checkingKing = true;
                return;
            }
        }
        checkingKing = false;
    }
    /**
     * This method validates that any position added by a piece is in the range of
     the board and that it doesn't already contain a piece.
     * For pieces other than the pawns, this method will validate that a piece can
     be taken if it is within its move set and is of the
     * opposite colour. For the pawn pieces, this method validates that the
     position a pawn can move to as part of its attacking
     * moves contains a piece and is of the opposite colour.
     *
     * An implementation of this abstract class will call it's generatePositions(),
     which calls this method which in turn calls
     * setCheckingKing().
     * generatePositions() -> validate() -> setCheckingKing()
     */
    protected void validate(ChessPiece[][] board) {
        //Clear the potentialTakes array.
        potentialTakes.clear();
        //Pawns.
        if(getPiece() == Pieces.BPAWN) { //Black pawn.
            int y1 = (int)getPosition().y-1;
            int x1 = (int)getPosition().x+1;
            if(x1 < 8 && x1 >= 0 && y1 < 8 && y1 >= 0)
                if(board[y1][x1] != null)
                    if(board[y1][x1].getPiece().toString().charAt(0) !=
                            getPiece().toString().charAt(0)) //Opposite colour.
                        potentialTakes.add(new Vector2(x1, y1));
            int y2 = (int)getPosition().y-1;
            int x2 = (int)getPosition().x-1;
            if(x2 < 8 && x2 >= 0 && y2 < 8 && y2 >= 0)
                if(board[y2][x2] != null)
                    if(board[y2][x2].getPiece().toString().charAt(0) !=
                            getPiece().toString().charAt(0)) //Opposite colour.
            potentialTakes.add(new Vector2(x2, y2));
        }
        if(getPiece() == Pieces.WPAWN) { //White pawn.
            int y1 = (int)getPosition().y+1;
            int x1 = (int)getPosition().x+1;
            if(x1 < 8 && x1 >= 0 && y1 < 8 && y1 >= 0)
                if(board[y1][x1] != null)
                    if(board[y1][x1].getPiece().toString().charAt(0) !=
                            getPiece().toString().charAt(0)) //Opposite colour.
                        potentialTakes.add(new Vector2(x1, y1));
            int y2 = (int)getPosition().y+1;
            int x2 = (int)getPosition().x-1;
            if(x2 < 8 && x2 >= 0 && y2 < 8 && y2 >= 0)
                if(board[y2][x2] != null)
                    if(board[y2][x2].getPiece().toString().charAt(0) !=
                            getPiece().toString().charAt(0)) //Opposite colour.
                        potentialTakes.add(new Vector2(x2, y2));
        }
        for(Vector2 pos : tempPos) {
            if(pos.x < 8 && pos.x >= 0 && pos.y < 8 && pos.y >= 0) { //They are positions on the board.
                if(board[(int)pos.y][(int)pos.x] == null) { //There are no pieces in this position.availablePositions.add(pos);
                } else { //If not null, piece can be taken if opposite colour.
                    if(getPiece() != Pieces.WPAWN && getPiece() != Pieces.BPAWN) {
//Forward moves for pawn cannot take a piece.
                        if (board[(int) pos.y][(int)
                                pos.x].getPiece().toString().charAt(0) != getPiece().toString().charAt(0)) {
                            potentialTakes.add(pos);
                        }
                    }
                }
            }
        }
        //Check if piece is now checking the king.
        setCheckingKing(board);
    }
    /**
     * A method that retrieves all available positions and potential takes for this
     piece.
     * @return an ArrayList of all moves for this piece.
     */
    public ArrayList<Vector2> getAllPotentialMoves() {
        ArrayList<Vector2> returnList = new ArrayList<Vector2>(potentialTakes);
        returnList.addAll(availablePositions);
        return returnList;
    }
    /**
     * The abstract method implemented by child classes to generate piece specific
     movements.
     * @param board the 2D board array.
     */
    public abstract void generatePositions(ChessPiece[][] board);
    public Pieces getPiece() {
        return piece;
    }
    public Vector2 getPosition() {
        return position;
    }
    public void setPosition(Vector2 position) {
        this.position = position;
    }
    @Override
    public void dispose() {
        pieceTexture.dispose();
    }
}
