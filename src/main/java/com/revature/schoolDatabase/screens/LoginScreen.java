package com.revature.schoolDatabase.screens;

import com.revature.schoolDatabase.models.Faculty;
import com.revature.schoolDatabase.models.Person;
import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.screens.menus.FacultyMenu;
import com.revature.schoolDatabase.screens.menus.StudentMenu;
import com.revature.schoolDatabase.services.UserService;
import com.revature.schoolDatabase.util.ScreenRouter;

import java.io.BufferedReader;

public class LoginScreen extends Screen {

    private final UserService userService;

    public LoginScreen(BufferedReader consoleReader, ScreenRouter router, UserService userService) {
        super("LoginScreen", "/login", consoleReader, router);
        this.userService = userService;
    }

    @Override
    public void render() throws Exception {

        System.out.println("\nLogin to your account!");

        System.out.print("Username: ");
        String username = consoleReader.readLine();

        System.out.print("Password: ");
        String password = consoleReader.readLine();

        // TODO follow to UserService -> expand later
        Person newPerson = userService.login(username, password);
        if (newPerson != null) {

            System.out.println(newPerson.getClass());
        } else System.out.println("newPerson is null");

        switch (username) {
            case "faculty":
                System.out.println("Logging in as faculty...");
                // Add FacultyMenu to HashSet
                router.addScreen(new FacultyMenu((Faculty) newPerson, consoleReader, router, userService));
                router.navigate("/faculty");
                break;
            case "student":
                System.out.println("Logging in as student...");
                // Add StudentMenu to HashSet
                router.addScreen(new StudentMenu((Student) newPerson, consoleReader, router, userService));
                router.navigate("/student");
                break;
            default:
                System.out.println("No user found, taking you back to main menu...");
                router.navigate("/main");
                break;
        }


    }
}
