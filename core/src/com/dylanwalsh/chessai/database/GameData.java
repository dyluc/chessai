package com.dylanwalsh.chessai.database;
import java.sql.Date;
/**
 * Stores data about a single game.
 */
public class GameData {
    /**
     * All private fields for this class, each representing a column in the games
     table, except name, which is retrieved
     * using the user_id.
     */
    private int gameId;
    private Date timeStamp;
    private int timeSurvived;
    private int score;
    private String moveHistory;
    private String name;
    private int win;
    public GameData(int gameId, Date timeStamp, int timeSurvived, int score, String
            moveHistory, String name, int win) {
        this.gameId = gameId;
        this.timeStamp = timeStamp;
        this.timeSurvived = timeSurvived;
        this.score = score;
        this.moveHistory = moveHistory;
        this.name = name;
        this.win = win;
    }
    /**
     * The following are all getter methods for the private fields of this class.
     This way I can be shore the values for the private
     * fields are not modified outside of this class.
     */
    public int getGameId() {
        return gameId;
    }
    public Date getTimeStamp() {
        return timeStamp;
    }
    public int getTimeSurvived() {
        return timeSurvived;
    }
    public int getScore() {
        return score;
    }
    public String getMoveHistory() {
        return moveHistory;
    }
    public String getName() {
        return name;
    }
    public int getWin() {
        return win;
    }
}
