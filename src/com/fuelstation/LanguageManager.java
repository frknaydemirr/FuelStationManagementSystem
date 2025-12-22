package com.fuelstation;

import java.util.Locale;
import java.util.ResourceBundle;

// Simplified Singleton class for text management.
public class LanguageManager {

    private static LanguageManager instance;
    private ResourceBundle resourceBundle;
    private static final String BUNDLE_NAME = "com.fuelstation.Messages";

    private LanguageManager() {
        // Force English locale
        Locale english = new Locale("en", "US");
        // Messages_en.properties loading
        resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, english);
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    //Returns the English text for a given key.@param key The key from the properties file @return The text
    public String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } 
        catch (Exception e) {
            System.err.println("Missing resource key: " + key);
            return "???" + key + "???";
        }
    }
}