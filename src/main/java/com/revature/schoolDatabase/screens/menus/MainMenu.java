package com.revature.schoolDatabase.screens.menus;

import com.revature.schoolDatabase.util.ProgramManager;
import com.revature.schoolDatabase.util.ScreenRouter;

import java.io.BufferedReader;

public class MainMenu extends Menu {

    public MainMenu(BufferedReader consoleReader, ScreenRouter router) {
        super("Main Menu", "/main", consoleReader, router, new String[] {"Login", "Register", "Exit"});
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
                ProgramManager.shutdown();
                break;
            default:
                System.out.println("You provided an invalid value, please try again.");

        }
    }
}
