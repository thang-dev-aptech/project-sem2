package com.example.gympro;

/**
 * Launcher class for jpackage native application
 * This class is needed because jpackage requires a non-module launcher
 */
public class Launcher {
    public static void main(String[] args) {
        GymProApp.main(args);
    }
}
