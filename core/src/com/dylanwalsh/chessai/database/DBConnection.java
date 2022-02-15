package com.dylanwalsh.chessai.database;
import java.sql.*;
import java.util.ArrayList;
//TODO: Make high score stored procedure
/**
 * This is a static class that deals with all functionality concerned with
 communicating with the database. This includes sending data
 * to be recorded in the database as well as retrieving data from the database. A
 GameData object is returned from each method where
 * data for a game can be easily accessed.
 */
public class DBConnection {
    //Values for communicating with the database.
    private static String connectionURL;
    private static String user;
    private static String pass;
    /**
     * The values needed to connect to the database are initialized in this method.
     This is only called once.
     */
    public static void initialize() {
        connectionURL = "jdbc:mysql://localhost:3306/chess_database";
        user = "mainuser1";
        pass = "t4tT3aU6l9jAwiir";
    }
    /**

     * A method to store details of a game in the database. The games table in the
     database is either updated or a new record is
     * added depending on whether this is a new game or an old game being re-saved.
     A new user is added to the users table depending
     * on whether this is a new user saving the game or not.
     * @param name the name of the user saving the game.
     * @param time the time elapsed for the player for the game being saved.
     * @param moves the number of moves made for the game being saved.
     * @param gameMoves a string representing the move history for the game being
    saved.
     * @param win the state of the game being saved. Win - 1, Loss - 0, Unfinished
    - 2
     * @param gameId the gameId of the game to update if this is a re-save,
    otherwise -1.
     * @return a new GameData object representing the saved game.
     */
    public static GameData saveGame(String name, int time, int moves, String
            gameMoves, int win, int gameId) {
        Connection dbConn = null;
        PreparedStatement dbStmt1 = null;
        PreparedStatement dbStmt2 = null;
        try {
            dbConn = DriverManager.getConnection(connectionURL, user, pass);
 /*
 I use 4 SQL statements in the saveGame() method, all of which are
parametrized queries. I do this so that values
 passed into this method can be easily inserted into the queries. The
first 2 queries are used for the users table and
 the final 2 are for the games table, though I do use a sub-query in
query2 to access the user_id from the users table.
 The CURDATE() function in mysql returns the current date in the format
YYYY-MM-DD.
 */
            String query1Check = "SELECT * FROM users WHERE name=?;";
            String query1 =
                    "INSERT INTO users(name) VALUES(?);";
            String query2 =
                    "INSERT INTO games(time_stamp, time_survived, score, move_history, user_id, win_loss) VALUES((SELECT CURDATE()), ?, ?, ?, (SELECT id FROM users WHERE name = ?), ?);";
            String query2Update =
                    "UPDATE games SET time_stamp=(SELECT CURDATE()), time_survived=?, score=?, move_history=?, win_loss=? WHERE id=?;";
            dbStmt1 = dbConn.prepareStatement(query1Check);
            dbStmt1.setString(1, name);
            ResultSet dbRes1 = dbStmt1.executeQuery();
            if(!dbRes1.next() && gameId==-1) { //Name doesn't exist in users andnot updating an older game.
                        dbStmt1 = dbConn.prepareStatement(query1);
                dbStmt1.setString(1, name);
                dbStmt1.executeUpdate();
            }
            if(gameId == -1) { //Newly save game.
                dbStmt2 = dbConn.prepareStatement(query2);
                dbStmt2.setInt(1, time);
                dbStmt2.setInt(2, moves);
                dbStmt2.setString(3, gameMoves);
                dbStmt2.setString(4, name); //Sub-query to look up user id using name
                dbStmt2.setInt(5, win);
            } else { //Save over old game.

                dbStmt2 = dbConn.prepareStatement(query2Update);
                dbStmt2.setInt(1, time);
                dbStmt2.setInt(2, moves);
                dbStmt2.setString(3, gameMoves);
                dbStmt2.setInt(4, win);
                dbStmt2.setInt(5, gameId);
            }
            dbStmt2.executeUpdate();
        } catch(SQLException e) {
            System.out.println("SQLException thrown, server may not be running!");
        } finally {
            try {
                if(dbConn != null)
                    dbConn.close();
                if(dbStmt1 != null)
                    dbStmt1.close();
                if(dbStmt2 != null)
                    dbStmt2.close();
            } catch(SQLException e2) {}
        }
        return getLatestGame(gameId);
    }
    /**
     * Retrieves latest game added/modified row in games table. If userId is -1,
     get the row with the highest id, else get the row
     * with that id.
     * @param gameId the id of the game if it is known. Otherwise -1.
     * @return a new GameData object representing the game retrieved from the
    database.
     */
    public static GameData getLatestGame(int gameId) {
        Date timeStamp = null;
        int timeSurvived = -1;
        int score = -1;
        String moveHistory = "";
        String name = "";
        int win = -1;
        Connection dbConn = null;
        Statement dbStmt1 = null;
        PreparedStatement dbStmt2 = null;
        try {
            dbConn = DriverManager.getConnection(connectionURL, user, pass);
            String query1 = "SELECT id, time_stamp, time_survived, score, move_history, (SELECT name FROM users WHERE id=user_id) AS name, win_loss FROM games ORDER BY id DESC LIMIT 0, 1;";
            String query2 = "SELECT time_stamp, time_survived, score, move_history, (SELECT name FROM users WHERE id=user_id) AS name, win_loss FROM games WHERE id=?";
            if(gameId==-1) { //Retrieve most recently added row.
                dbStmt1 = dbConn.createStatement();
                ResultSet dbRes = dbStmt1.executeQuery(query1);
                if(dbRes.next()) {
                    gameId = dbRes.getInt("id");
                    timeStamp = dbRes.getDate("time_stamp");
                    timeSurvived = dbRes.getInt("time_survived");
                    score = dbRes.getInt("score");
                    moveHistory = dbRes.getString("move_history");
                    name = dbRes.getString("name");
                    win = dbRes.getInt("win_loss");
                }
            } else { //Retrieve row with gameId.
                dbStmt2 = dbConn.prepareStatement(query2);
                dbStmt2.setInt(1, gameId);
                ResultSet dbRes = dbStmt2.executeQuery();
                if(dbRes.next()) {
                    timeStamp = dbRes.getDate("time_stamp");
                    timeSurvived = dbRes.getInt("time_survived");
                    score = dbRes.getInt("score");
                    moveHistory = dbRes.getString("move_history");
                    name = dbRes.getString("name");
                    win = dbRes.getInt("win_loss");
                }
            }
        } catch(SQLException e) {
            System.out.println("SQLException thrown, server may not be running!");
        } finally {
            try {
                if(dbConn != null)
                    dbConn.close();
                if(dbStmt1 != null)
                    dbStmt1.close();
                if(dbStmt2 != null)
                    dbStmt2.close();
            } catch(SQLException e2) {}
        }
        return new GameData(gameId, timeStamp, timeSurvived, score, moveHistory,
                name, win);
    }
    /**
     * This method retrieves all previously saved games in the database and returns
     them in the form of a list.
     * @return an ArrayList containing GameData objects.
     */
    public static ArrayList<GameData> getPastGames() {
        ArrayList<GameData> games = new ArrayList<GameData>();
        Connection dbConn = null;
        Statement dbStmt = null;
        try {
            dbConn = DriverManager.getConnection(connectionURL, user, pass);
            String query = "SELECT id, time_stamp, time_survived, score, move_history, (SELECT name FROM users WHERE id=user_id) AS name, win_loss FROM games;";
            dbStmt = dbConn.createStatement();
            ResultSet dbRes = dbStmt.executeQuery(query);
            while(dbRes.next()) {
                int gameId = dbRes.getInt("id");
                Date timeStamp = dbRes.getDate("time_stamp");
                int timeSurvived = dbRes.getInt("time_survived");
                int score = dbRes.getInt("score");
                String moveHistory = dbRes.getString("move_history");
                String name = dbRes.getString("name");
                int win = dbRes.getInt("win_loss");
                games.add(new GameData(gameId, timeStamp, timeSurvived, score,
                        moveHistory, name, win));
            }
        } catch(SQLException e) {
            System.out.println("SQLException thrown, server may not be running!");
        } finally {
            try {
                if(dbConn != null)
                    dbConn.close();
                if(dbStmt != null)
                    dbStmt.close();
            } catch(SQLException e2) {}
        }
        return games;
    }
}