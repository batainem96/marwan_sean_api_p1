package com.revature.schoolDatabase.screens.menus;

import com.revature.schoolDatabase.util.ScreenRouter;

import java.io.BufferedReader;

public class MainMenu extends Menu {

    public MainMenu(BufferedReader consoleReader, ScreenRouter router) {
        super("Main Menu", "/main", consoleReader, router, new String[] {"Login", "Register"});
    }

    @Override
    public void render() throws Exception {

        displayMenu();

        String userSelection = consoleReader.readLine();

        switch (userSelection) {

            case "1":
                router.navigate("/login");
                break;
            case "2":
                router.navigate("/register");
                break;
            case "3":
                System.out.println("Exiting application...");
                // figure a way to make the app shutdown
                // TODO this is ugly and bad practice, we will fix it later
                System.exit(0);
            default:
                System.out.println("You provided an invalid value, please try again.");

        }
    }
}
