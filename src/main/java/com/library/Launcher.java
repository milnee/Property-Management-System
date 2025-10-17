package com.library;

public class Launcher {
    public static void main(String[] args) {
        try {
            System.out.println("Starting Property Management Application...");
            PropertyApp.main(args);
        } catch (Exception e) {
            System.err.println("Error starting application:");
            e.printStackTrace();
        }
    }
}
