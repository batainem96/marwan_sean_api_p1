package com.revature.schoolDatabase.util;

import com.revature.schoolDatabase.screens.LoginScreen;
import com.revature.schoolDatabase.screens.RegisterScreen;
import com.revature.schoolDatabase.screens.menus.FacultyMenu;
import com.revature.schoolDatabase.screens.menus.MainMenu;
import com.revature.schoolDatabase.screens.menus.StudentMenu;
import com.revature.schoolDatabase.services.UserService;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProgramManager {
    // Variables
    private boolean programRunning;
    private final ScreenRouter router;

    // Constructors
    public ProgramManager() {
        programRunning = true;
        router = new ScreenRouter();
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        UserService userService = new UserService();

        // Add a number of screens we will use to the router HashSet
        // TODO Potentially fix it to only instantiate screens as they are used.
        router.addScreen(new MainMenu(consoleReader, router));
        router.addScreen(new RegisterScreen(consoleReader, router));
        router.addScreen(new LoginScreen(consoleReader, router, userService));


    }

    // Methods

    /**
     * This is the main while loop which enables the program to run. The screen router is
     * initialized at the main menu, then is continuously re-rendered until an exception is
     * thrown or programRunning is set to false;
     *
     * @param none
     * @return none
     */
    public void start() {
        System.out.println("Welcome to Revature University!");
        router.navigate("/main");

        while (programRunning) {
            try {
                router.getCurrentScreen().render();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
