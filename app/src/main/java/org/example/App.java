package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Application started.");

        String url = Config.getDbUrl();
        String user = Config.getDbUser();
        String pass = Config.getDbPass();

        logger.info("Connecting to database at URL: {}", url);

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {

            logger.info("Successfully connected to the database.");

            String sql = "SELECT * FROM trade_details LIMIT 1"; // Update your table name here
            logger.debug("Executing query: {}", sql);

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                String account_number = rs.getString("account_number"); // Change column names as per your table
                String symbol = rs.getString("symbol");
                logger.info("Query result - account_number: {}, name: {}", account_number, symbol);
                System.out.printf("Row: account_number=%s, name=%s%n", account_number, symbol);
            } else {
                logger.warn("No rows found in the table.");
                System.out.println("No rows found.");
            }
        } catch (Exception e) {
            logger.error("An error occurred while accessing the database.", e);
            e.printStackTrace();
        }

        logger.info("Application finished.");
    }

    public String getGreeting() {
        return "Hello from App!";
    }
}
