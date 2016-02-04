package com.nl.util.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBAccess {

    protected Statement statement;
    protected Connection connection;

    /**
     * Constructor, Create a new DBAccess object.
     *
     * @param connection a certain connection.
     */
    public DBAccess(Connection connection) {
        this.connection = connection;
    }

    /**
     * Close select
     *
     * @throws SQLException this method
     */
    public void closeSelect() throws SQLException {
        if (statement != null)
            statement.close();
    }

    /**
     * open select
     *
     * @param sql sql string
     * @return ResultSet
     * @throws SQLException this method
     */
    public ResultSet openSelect(String sql) throws SQLException {
//    statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        return rs;
    }

    public ResultSet openSelect(String sql, String type) throws SQLException {
        if (type.equals("oracle")) {
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        }  else {
            statement = connection.createStatement();
        }
        ResultSet rs = statement.executeQuery(sql);
        return rs;
    }

    /**
     * Run a sql string
     *
     * @param sql sql string
     * @return int
     * @throws SQLException this method
     */
    public int runSql(String sql) throws SQLException {
        statement = connection.createStatement();
        int result = statement.executeUpdate(sql);
        statement.close();
        return result;
    }
}
