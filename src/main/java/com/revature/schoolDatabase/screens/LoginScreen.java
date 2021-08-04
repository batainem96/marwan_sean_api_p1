package com.revature.schoolDatabase.screens;

import com.revature.schoolDatabase.util.ScreenRouter;

import java.io.BufferedReader;

public class LoginScreen extends Screen {

//    private final UserService userService;

    public LoginScreen(BufferedReader consoleReader, ScreenRouter router) {
        super("LoginScreen", "/login", consoleReader, router);
    }

    @Override
    public void render() throws Exception {

        System.out.println("\nLogin to your account!\n");

        System.out.print("Username: ");
        String username = consoleReader.readLine();

        System.out.print("Password: ");
        String password = consoleReader.readLine();

//        AppUser authUser = userService.login(username, password);

        switch (username) {
            case "faculty":
                System.out.println("Logging in as faculty...");
                router.navigate("/faculty");
                break;
            case "student":
                System.out.println("Logging in as student...");
                router.navigate("/student");
                break;
            default:
                System.out.println("No user found, taking you back to main menu...");
                router.navigate("/main");
                break;
        }


    }
}
