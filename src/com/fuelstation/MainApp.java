package com.fuelstation;

import javax.swing.SwingUtilities;

// Main application entry point. Starts the LoginScreen first.
public class MainApp {
    public static void main(String[] args) {
        // Ensure the UI is created and updated on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}