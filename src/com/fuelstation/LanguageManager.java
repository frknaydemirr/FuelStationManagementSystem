package com.fuelstation;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {

    private static LanguageManager instance;
    private ResourceBundle resourceBundle;
    private static final String BUNDLE_NAME = "com.fuelstation.Messages";

    private LanguageManager() {
        Locale english = new Locale("en", "US");
        resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, english);
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

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