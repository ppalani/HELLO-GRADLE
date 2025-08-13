package org.example;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.load();

    public static String getDbUrl() {
        return dotenv.get("DB_URL");
    }

    public static String getDbUser() {
        return dotenv.get("DB_USER");
    }

    public static String getDbPass() {
        return dotenv.get("DB_PASS");
    }
}