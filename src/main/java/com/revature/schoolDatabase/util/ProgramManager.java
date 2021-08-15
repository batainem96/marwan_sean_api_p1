package com.revature.schoolDatabase.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.schoolDatabase.repositories.CourseRepository;
import com.revature.schoolDatabase.repositories.UserRepository;
import com.revature.schoolDatabase.services.CourseService;
import com.revature.schoolDatabase.services.UserService;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProgramManager {

    // Text colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BRIGHT_BLUE = "\u001B[94m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_BRIGHT_GREEN = "\u001B[92m";

    public ProgramManager() {

        // Through Dependency Injection, we will use these variables throughout the application
        UserRepository userRepository = new UserRepository(new ObjectMapper());
        UserService userService = new UserService(userRepository);
        CourseRepository courseRepository = new CourseRepository(new ObjectMapper());
        CourseService courseService = new CourseService(courseRepository);

    }

    /**
     * This is the main while loop which enables the program to run. The screen router is
     * initialized at the main menu, then is continuously re-rendered until an exception is
     * thrown or programRunning is set to false;
     *
     */
    public void start() {

    }

    public static void shutdown() {

    }

}
