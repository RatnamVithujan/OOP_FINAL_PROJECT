package com.stockms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Port 3306 is the default MySQL port
    private static final String URL = "jdbc:mysql://localhost:3306/stock_management_system";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Add your MySQL root password here if you set one

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Loads the MySQL database driver into memory
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL Driver not found! Make sure you added the connector JAR file to Libraries.", e);
            }
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}