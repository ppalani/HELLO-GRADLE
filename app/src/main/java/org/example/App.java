package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tradeUtilities;

public class App {

    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Application started.");

        String url = Config.getDbUrl();
        String user = Config.getDbUser();
        String pass = Config.getDbPass();

        // Everything done inside tradeUtilities
        tradeUtilities.loadAndCalculateProfit(url, user, pass);

        logger.info("Application finished.");

        
    }
        public String getGreeting() {
        return "Hello from App!";
    }
}
