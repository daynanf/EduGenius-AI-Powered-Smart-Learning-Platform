// FILE: src/main/java/com/edugenius/config/AppConfig.java
package com.edugenius.config;

import java.io.*;
import java.util.Properties;

/**
 * Configuration loader singleton
 * Loads config.properties from resources folder
 */
public class AppConfig {
    
    private static AppConfig instance;
    private Properties properties;
    
    private AppConfig() {
        properties = new Properties();
        loadConfig();
    }
    
    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }
    
    private void loadConfig() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            
            if (input == null) {
                System.err.println("[ERROR] config.properties not found in resources folder!");
                createDefaultConfig();
                return;
            }
            
            properties.load(input);
            System.out.println("[INFO] Configuration loaded successfully");
            
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load config: " + e.getMessage());
            createDefaultConfig();
        }
    }
    
    private void createDefaultConfig() {
        System.out.println("[INFO] Creating default configuration...");
        properties.setProperty("db.host", "localhost");
        properties.setProperty("db.port", "3306");
        properties.setProperty("db.name", "edugenius_db_new");
        properties.setProperty("db.user", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("groq.api.key", "");
        properties.setProperty("groq.model", "llama-3.3-70b-versatile");
        properties.setProperty("groq.max.tokens.quiz", "1200");
        properties.setProperty("groq.max.tokens.tutor", "800");
        properties.setProperty("groq.max.tokens.plan", "600");
        properties.setProperty("app.name", "EduGenius");
        properties.setProperty("app.version", "1.0.0");
        properties.setProperty("app.window.width", "1200");
        properties.setProperty("app.window.height", "750");
        properties.setProperty("app.window.min.width", "1000");
        properties.setProperty("app.window.min.height", "650");
    }
    
    public String get(String key) {
        return properties.getProperty(key);
    }
    
    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public int getInt(String key) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }
    
    // Database configuration getters
    public String getDbHost() { return get("db.host", "localhost"); }
    public int getDbPort() { return getInt("db.port", 3306); }
    public String getDbName() { return get("db.name", "edugenius_db_new"); }
    public String getDbUser() { return get("db.user", "root"); }
    public String getDbPassword() { return get("db.password", ""); }
    
    public String getDbUrl() {
        return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                getDbHost(), getDbPort(), getDbName());
    }
    
    // Groq API configuration getters
    public String getGroqApiKey() { return get("groq.api.key", ""); }
    public String getGroqModel() { return get("groq.model", "llama-3.3-70b-versatile"); }
    public int getGroqMaxTokensQuiz() { return getInt("groq.max.tokens.quiz", 1200); }
    public int getGroqMaxTokensTutor() { return getInt("groq.max.tokens.tutor", 800); }
    public int getGroqMaxTokensPlan() { return getInt("groq.max.tokens.plan", 600); }
    
    // Application configuration getters
    public String getAppName() { return get("app.name", "EduGenius"); }
    public String getAppVersion() { return get("app.version", "1.0.0"); }
    public int getWindowWidth() { return getInt("app.window.width", 1200); }
    public int getWindowHeight() { return getInt("app.window.height", 750); }
    public int getWindowMinWidth() { return getInt("app.window.min.width", 1000); }
    public int getWindowMinHeight() { return getInt("app.window.min.height", 650); }
    
    /**
     * Validate critical configuration
     * Returns true if all required settings are present
     */
    public boolean validateConfig() {
        boolean valid = true;
        
        if (getDbPassword().isEmpty()) {
            System.err.println("[WARNING] MySQL password is empty in config.properties");
        }
        
        if (getGroqApiKey().isEmpty()) {
            System.err.println("[WARNING] Groq API key is missing in config.properties");
            valid = false;
        }
        
        return valid;
    }
}