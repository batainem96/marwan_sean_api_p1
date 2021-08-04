package com.revature.schoolDatabase.util;

import com.revature.schoolDatabase.screens.Screen;

import java.util.HashSet;
import java.util.Set;

public class ScreenRouter {
    // Variables
    private Screen currentScreen;
    private Set<Screen> screens = new HashSet<>();

    // Getters and Setters
    public Screen getCurrentScreen() {
        return currentScreen;
    }

    public Set<Screen> getScreens() {
        return screens;
    }

    // Methods
    /**
     * Adds a screen to the HashSet
     *
     * @param screen
     * @return
     */
    public ScreenRouter addScreen(Screen screen) {
        screens.add(screen);
        return this;
    }

    /**
     * Removes a screen from the HashSet
     *
     * @param screen
     */
    public void removeScreen(Screen screen) {
        screens.remove(screen);
    }

    public void navigate(String route) {
        for (Screen screen : screens) {
            if (screen.getRoute().equals(route)) {
                currentScreen = screen;
                break;
            }
        }
    }



}