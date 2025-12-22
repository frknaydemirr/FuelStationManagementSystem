package com.fuelstation;

// Configuration interface for database connection parameters.
public interface DBConfig {
    
    // JDBC URL for the MySQL database. Replace 'fuelstation_db' with your actual database name if needed.
    String DB_URL = "jdbc:mysql://localhost:3306/fuelstations";
    // Database user name 
    String DB_USER = "root"; 
    // Database password 
    String DB_PASSWORD = "YeniŞifre123!";
    // The driver class name for the MySQL JDBC connector.
    String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
}