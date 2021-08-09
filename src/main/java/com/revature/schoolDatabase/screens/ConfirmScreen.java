package com.revature.schoolDatabase.screens;

import com.revature.schoolDatabase.services.CourseService;
import com.revature.schoolDatabase.services.UserService;
import com.revature.schoolDatabase.util.ScreenRouter;

import java.io.BufferedReader;

public class ConfirmScreen extends Screen{
    // Constructors
    public ConfirmScreen(BufferedReader consoleReader, ScreenRouter router, UserService userService, CourseService courseService) {
        super("ConfirmScreen", "/confirm", consoleReader, router);
    }

    @Override
    public void render() throws Exception {

    }
}
