package com.dylanwalsh.chessai.ai;
import com.badlogic.gdx.math.Vector2;
import com.dylanwalsh.chessai.entities.ChessPiece;
import com.dylanwalsh.chessai.util.PieceMovements;
import java.util.ArrayList;
/**
 * This class deals with all functionality concerned with generating the best move
 for the AI. It does this by using the minimax
 * algorithm with the alpha-beta pruning enhancement.
 */
public class GameAI {
    //The depth to explore the search tree.
    private int searchDepth = 3;
    //A copy of the board.
    private ChessPiece[][] board;
    //The piece and move evaluated by AI for the current turn.
    private ChessPiece piece;
    private Vector2 move;
    /**
     * This method is called to assign values to piece and move.
     * @param board the 2D board array.
     */
    public void calculateBestMove(ChessPiece[][] board) {
        this.board = board.clone();
        piece = null;
        move = null;
        miniMaxAlphaBeta(searchDepth, -11111, 11111, false);
    }
    /**
     * The minimax search algorithm, which traverses the search tree of potential
     future moves and evaluates the best move for any
     * given game state (state of the board) down to a specific depth. This is a
     recursive algorithm which calls itself.
     * @param depth the depth to explore the search tree.
     * @param alpha alpha value for improved minimax algorithm.
     * @param beta beta value for improved minimax algorithm.
     * @param maxi true if this is the maximizing player, false if this is the
    minimizer.
     * @return a score/value measuring the favorability of a particular node at a
    particular depth.
     */
    private int miniMaxAlphaBeta(int depth, int alpha, int beta, boolean maxi) {
        if(maxi) {

            if(depth == 0) return evaluateBoard();
            int max = -9999;
            for(ChessPiece p : getPieces(maxi)) for(Vector2 m :
                    p.getAllPotentialMoves()) {
                PieceMovements.move(board, p, (int)m.x, (int)m.y);
                int score = miniMaxAlphaBeta(depth-1, alpha, beta, !maxi);
                try {
                    if(p.getFriendlyKing(board).isInCheck(board)) score = -9999;
                } catch(NullPointerException e) {
                    score = -9999;
                }
                PieceMovements.undo();
                if(score > max) {
                    max = score;
                    if(depth == searchDepth) {
                        piece = p;
                        move = m;
                    }
                }
                alpha = Math.max(alpha, max);
                if(beta<=alpha) return max;
            }
            return max;
        } else {
            if(depth == 0) return -evaluateBoard();
            int min = 9999;
            for(ChessPiece p : getPieces(maxi)) for(Vector2 m :
                    p.getAllPotentialMoves()) {
                PieceMovements.move(board, p, (int)m.x, (int)m.y);
                int score = miniMaxAlphaBeta(depth-1, alpha, beta, !maxi);
                try {
                    if(p.getFriendlyKing(board).isInCheck(board)) score = 9999;
                } catch(NullPointerException e) {
                    score = 9999;
                }
                PieceMovements.undo();
                if(score < min) {
                    min = score;
                    if(depth == searchDepth) {
                        piece = p;
                        move = m;
                    }
                }
                beta = Math.min(beta, min);
                if(beta<=alpha) return min;
            }
            return min;
        }
    }
    /**
     * The method used to evaluate the state of the board, which considers all
     pieces on the board. It uses a pieces relative piece
     * value as well as looking up it piece square table to determine its value.
     *
     * @return an evaluation/integer value of the board.
     */
    private int evaluateBoard() {
        int total = 0;
        for(ChessPiece[] row : board) {
            for(ChessPiece p : row) {
                if(p!=null) {
                    total += getPieceValue(p);
                }
            }
        }
        return total;
    }

    /**
     * This method retrieves a given pieces value on the board. It uses the piece's
     relative piece value and looks up it's piece
     * square table value to determine a value for the piece.
     * @param p the ChessPiece to evaluate.
     * @return the chess piece's value on the board
     */
    private int getPieceValue(ChessPiece p) {
        int value = 0;
        value += getPieceRelativeValue(p);
        value += getPieceSquareTableValue(p);
        return (p.getPiece().toString().charAt(0)=='W'?value:-value);
    }
    /**
     * This method looks up a piece's position on it's piece square table.
     * @param p the piece to look up
     * @return a value at the piece's current position on it's piece square table
    representing how favorable that position is for that piece.
     */
    private int getPieceSquareTableValue(ChessPiece p) {
        short[][] table = null;
        switch(p.getPiece()) {
            case WPAWN:
                table =
                        PieceSquareTables.reverseTable(PieceSquareTables.PAWNTABLE);
                break;
            case BPAWN:
                table = PieceSquareTables.PAWNTABLE;
                break;
            case WKNIGHT:
                table =
                        PieceSquareTables.reverseTable(PieceSquareTables.KNIGHTTABLE);
                break;
            case BKNIGHT:
                table = PieceSquareTables.KNIGHTTABLE;
                break;
            case WROOK:
                table =
                        PieceSquareTables.reverseTable(PieceSquareTables.ROOKTABLE);
                break;
            case BROOK:
                table = PieceSquareTables.ROOKTABLE;
                break;
            case WBISHOP:
                table =
                        PieceSquareTables.reverseTable(PieceSquareTables.BISHOPTABLE);
                break;
            case BBISHOP:
                table = PieceSquareTables.BISHOPTABLE;
                break;
            case WQUEEN:
                table =
                        PieceSquareTables.reverseTable(PieceSquareTables.QUEENTABLE);
                break;
            case BQUEEN:
                table = PieceSquareTables.QUEENTABLE;
                break;
            case WKING:
                table =
                        PieceSquareTables.reverseTable(PieceSquareTables.KINGTABLE);
                break;
            case BKING:
                table = PieceSquareTables.KINGTABLE;
                break;
        }
        return table[(int)p.getPosition().y][(int)p.getPosition().x];

    }
    /**
     * Looks up this pieces relative value.
     * @param p the piece to look up.
     * @return the piece's relative value.
     */
    private int getPieceRelativeValue(ChessPiece p) {
        switch(p.getPiece()) {
            case WPAWN:
            case BPAWN:
                return RelativePieceValues.PAWN;
            case WKNIGHT:
            case BKNIGHT:
                return RelativePieceValues.KNIGHT;
            case WROOK:
            case BROOK:
                return RelativePieceValues.ROOK;
            case WBISHOP:
            case BBISHOP:
                return RelativePieceValues.BISHOP;
            case WQUEEN:
            case BQUEEN:
                return RelativePieceValues.QUEEN;
            case WKING:
            case BKING:
                return RelativePieceValues.KING;
        }
        return 0;
    }
    /**
     * @param isWhite if true, returns all white pieces, if false returns all black
    pieces.
     * @return a list of all white or black pieces on the board.
     */
    private ArrayList<ChessPiece> getPieces(boolean isWhite) {
        ArrayList<ChessPiece> pieces = new ArrayList<ChessPiece>();
        for(ChessPiece[] row : board)
            for(ChessPiece p : row)
                if(p != null) {
                    if(isWhite) {
                        if(p.getPiece().toString().charAt(0) == 'W') pieces.add(p);
                    } else {
                        if(p.getPiece().toString().charAt(0) == 'B') pieces.add(p);
                    }
                }
        return pieces;
    }
    /**
     * @return the next piece to move determined by the minimax algorithm.
     */
    public ChessPiece getPiece() { return piece; }
    /**
     * @return the position to move the piece to determined by the minimax
    algorithm.
     */
    public Vector2 getMove() { return move; }
}
