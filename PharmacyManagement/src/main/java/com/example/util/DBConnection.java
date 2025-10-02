package com.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {// Database connection parameters
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:ORCL";
    private static final String DB_USER = "c##scott";
    private static final String DB_PASSWORD = "tiger";

    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");// Load Oracle JDBC Driver
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException { // Get a connection to the database
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void closeConnection(Connection conn) { // Close the database connection
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}