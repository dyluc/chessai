package com.dylanwalsh.chessai.util;
import com.badlogic.gdx.math.Vector2;
import com.dylanwalsh.chessai.entities.ChessPiece;
import com.dylanwalsh.chessai.entities.chesspieces.King;
import com.dylanwalsh.chessai.entities.chesspieces.Pawn;
import com.dylanwalsh.chessai.entities.chesspieces.Rook;
import com.dylanwalsh.chessai.input.GameInput;
import java.util.Stack;
/**
 * A static class used in GameAI and ChessPiece that performs and undoes piece
 movements. It makes use of a stack to track the
 * history of moved pieces.
 */
public class PieceMovements {
    //This is the piece move stack. A move is pushed onto the move stack when a new piece is moved. The piece is removed from the
    //stack when a move is undone. Piece movements that are not undone are left in the stack.
    private static Stack<PieceMovement> moveStack = new Stack<PieceMovement>();
    private static String moveString = "";
    private static ChessPiece[][] chessBoard;
    public static ChessPiece move(ChessPiece[][] board, ChessPiece piece, int posX,
                                  int posY) {
        chessBoard = board;
        PieceMovement p = null;
        if(piece.getPiece() == ChessPiece.Pieces.BKING || piece.getPiece() ==
                ChessPiece.Pieces.WKING) {
            King castleKing = (King)piece;
            Rook castleRook = null;
            if(castleKing.canCastleLeft() && posX ==
                    castleKing.getCastleLeftPosition().x && posY ==
                    castleKing.getCastleLeftPosition().y)
                castleRook = castleKing.getCastleLeftRook();
            else if(castleKing.canCastleRight() && posX ==
                    castleKing.getCastleRightPosition().x && posY ==
                    castleKing.getCastleRightPosition().y)
                castleRook = castleKing.getCastleRightRook();
            if(castleKing != null && castleRook != null) { //Castling move.
                //Push king and then rook onto the move stack (has to be in this order as king is found in undo method by popping
                //off the stack the next movement after a castling rook).
                p = moveStack.push(new PieceMovement(piece, posX, posY,
                        board[posY][posX], true));
                moveStack.push(new PieceMovement(castleRook,
                        (int)castleRook.getCastlePosition().x, (int)castleRook.getCastlePosition().y,
                        board[posY][posX], true));
                //Set both positions to null first.
                board[(int)castleRook.getPosition().y][(int)castleRook.getPosition().x] = null;

                board[(int)castleKing.getPosition().y][(int)castleKing.getPosition().x] = null;
                //Update rook position.
                board[(int)castleRook.getCastlePosition().y][(int)castleRook.getCastlePosition().x]
                        = castleRook;
                castleRook.setPosition(castleRook.getCastlePosition());
                //Update king position.
                board[posY][posX] = castleKing;
                castleKing.setPosition(new Vector2(posX, posY));
            } else if(castleRook == null) { //Regular king movement.
                //Here the king movement is pushed onto the move stack.
                p = moveStack.push(new PieceMovement(piece, posX, posY,
                        board[posY][posX], false));
                board[p.getFromY()][p.getFromX()] = null; //Set previous position to null.
                        board[posY][posX] = piece; //Update new position on board array.
                piece.setPosition(new Vector2(posX, posY)); //Set piece position vector.
            }
        } else {
            //Push a regular piece movement onto the stack.
            p = moveStack.push(new PieceMovement(piece, posX, posY,
                    board[posY][posX], false));
            board[p.getFromY()][p.getFromX()] = null; //Set previous position to null
            board[posY][posX] = piece; //Update new position on board array.
            piece.setPosition(new Vector2(posX, posY)); //Set piece position vector.
        }
        //Generate new positions for every piece.
        for(ChessPiece[] row : board) {
            for(ChessPiece pi : row) {
                if(pi != null) pi.generatePositions(board);
            }
        }
        return p.getPieceAtPosition(); //So it can get disposed if it was taken.
    }
    /**
     * Undoes the previous movement. This method calls generatePositions on every
     piece after it undoes the previous movement.
     */
    public static void undo() {
        if(!moveStack.isEmpty()) {
            //Pop the next movement off the move stack.
            PieceMovement p = moveStack.pop();
            if( (p.getPiece().getPiece() == ChessPiece.Pieces.WROOK ||
                    p.getPiece().getPiece() == ChessPiece.Pieces.BROOK) && p.isCastle() == true) {
                //The PieceMovement object after a castling rook movement has been popped off the stack should be the movement of the
                //king in the castling move.
                PieceMovement pKing = moveStack.pop();
                chessBoard[pKing.getFromY()][pKing.getFromX()] = pKing.getPiece();
//Set previous position to piece.
                chessBoard[pKing.getToY()][pKing.getToX()] =
                        pKing.getPieceAtPosition(); //Will be null if no piece was at that position.
                pKing.getPiece().setPosition(new Vector2(pKing.getFromX(),
                        pKing.getFromY())); //Update piece position vector.
                //Rook will get reset below.
            }
            //Undo movement.
            chessBoard[p.getFromY()][p.getFromX()] = p.getPiece(); //Set previous position to piece.
                    chessBoard[p.getToY()][p.getToX()] = p.getPieceAtPosition(); //Will be null if no piece was at that position.
            p.getPiece().setPosition(new Vector2(p.getFromX(), p.getFromY()));
//Update piece position vector.
            //Finish by generating all new positions for every piece.
            for(ChessPiece[] row : chessBoard) {
                for(ChessPiece piece : row) {
                    if(piece != null) piece.generatePositions(chessBoard);
                }
            }
        }
    }
    /**
     * This method returns a combination of all moves made throughout the course of
     the game so far in the form of a String.
     * The format for a move would be:
     * previousx previousy newx newy (promotePawn character)
     *
     * So a string that looks like 3545 would mean the piece at 6D moved to 6E
     * A string that looks like 6667Q would mean the white pawn at G7 moved to G8
     and was promoted to a Queen.
     * @return a string representing the game move history so far.
     */
    public static String getGameMoves() {
        return moveString;
    }
    public static void resetMoves() { moveStack.empty(); moveString=""; }
    /**
     * Records the last game added to the move stack in the moveString.
     */
    public static void recordLastMove() {
        PieceMovement m = moveStack.lastElement();
        moveString += Integer.toString(m.getFromX()) +
                Integer.toString(m.getFromY()) + Integer.toString(m.getToX()) +
                Integer.toString(m.getToY());
    }
    public static void promoteWhitePawn(Pawn pawn, char promotePawn, GameInput in)
    {
        char promotedPawn = in.promotePawn(pawn, promotePawn);
        moveString += promotedPawn;
    }
    public static void promoteBlackPawn(Pawn pawn, char promotePawn) {
        char promotedPawn = pawn.promote(chessBoard, promotePawn);
        moveString += promotedPawn;
    }
}