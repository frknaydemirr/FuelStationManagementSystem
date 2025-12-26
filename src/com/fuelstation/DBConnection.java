package com.fuelstation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane; 


public class DBConnection {

    public static Connection getConnection() {
        Connection connection = null;
        
        try {
            Class.forName(DBConfig.DRIVER_CLASS);
            
           
            connection = DriverManager.getConnection(DBConfig.DB_URL, DBConfig.DB_USER, DBConfig.DB_PASSWORD);
            
        } 
        catch (ClassNotFoundException e) {
            
            String message = "ERROR: MySQL JDBC Driver not found. Please ensure 'mysql-connector-j.jar' is added.";
            System.err.println(message);
            JOptionPane.showMessageDialog(null, message, "FATAL Driver Error", JOptionPane.ERROR_MESSAGE);
            
        } 
        catch (SQLException e) {
            String message = "ERROR: Database connection failed. Check DBConfig and ensure MySQL server is running. Message: " + e.getMessage();
            System.err.println(message);
            JOptionPane.showMessageDialog(null, message, "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        return connection;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            }
            catch (SQLException e) {
                System.err.println("Error while closing connection: " + e.getMessage());
            }
        }
    }
}