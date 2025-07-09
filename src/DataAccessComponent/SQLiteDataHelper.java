/*
|-------------------------------------|
| © 2024 EPN-FIS, All rights reserved |
| jonathan.pagual@epn.edu.ec          |
|-------------------------------------|
Autor: Jonathan Pagual
Fecha: 27-02-2024
Script: Data Helper para SQLite
*/

package DataAccessComponent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class SQLiteDataHelper {
    private static String DBPathConnection = "jdbc:sqlite:database\\InkHarmony.sqlite";
    private static Connection conn = null;

    protected SQLiteDataHelper() {}

    protected static synchronized Connection openConnection() throws Exception {
        try {
            if (conn == null)
            System.err.println("path:"+DBPathConnection);
                conn = DriverManager.getConnection(DBPathConnection);
        } catch (SQLException e) {
            throw e;
        }
        return conn;
    }

    protected static void closeConnection() throws Exception {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            throw e;
        }
    }
}
