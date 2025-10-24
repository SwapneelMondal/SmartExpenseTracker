package com.tracker;

import java.sql.*;

public class DBHelper {
    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:expenses.db");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
