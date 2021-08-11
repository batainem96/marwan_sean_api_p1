package com.revature.schoolDatabase.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.schoolDatabase.repositories.CourseRepository;
import com.revature.schoolDatabase.repositories.UserRepository;
import com.revature.schoolDatabase.screens.LoginScreen;
import com.revature.schoolDatabase.screens.RegisterScreen;
import com.revature.schoolDatabase.screens.menus.MainMenu;
import com.revature.schoolDatabase.services.CourseService;
import com.revature.schoolDatabase.services.UserService;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProgramManager {
    // Variables
    private static boolean programRunning;
    private final ScreenRouter router;

    // Text colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BRIGHT_BLUE = "\u001B[94m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_BRIGHT_GREEN = "\u001B[92m";

    // Constructors
    public ProgramManager() {
        programRunning = true;

        // Through Dependency Injection, we will use these variables throughout the application
        router = new ScreenRouter();
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        UserRepository userRepository = new UserRepository(new ObjectMapper());
        UserService userService = new UserService(userRepository);
        CourseRepository courseRepository = new CourseRepository(new ObjectMapper());
        CourseService courseService = new CourseService(courseRepository);

        // Add a number of screens we will use to the router HashSet
        // Others are added on a need basis
        router.addScreen(new MainMenu(consoleReader, router));
        router.addScreen(new RegisterScreen(consoleReader, router, userService));
        router.addScreen(new LoginScreen(consoleReader, router, userService, courseService));
    }

    // Methods
    /**
     * This is the main while loop which enables the program to run. The screen router is
     * initialized at the main menu, then is continuously re-rendered until an exception is
     * thrown or programRunning is set to false;
     *
     */
    public void start() {
        System.out.println(ANSI_BRIGHT_GREEN + "\nWelcome to Revature University!" + ANSI_RESET);
        router.navigate("/main");

        while (programRunning) {
            try {
                router.getCurrentScreen().render();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void shutdown() {
        programRunning = false;
    }

}
