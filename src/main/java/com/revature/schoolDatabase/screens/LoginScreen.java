package com.revature.schoolDatabase.screens;

import com.revature.schoolDatabase.models.Faculty;
import com.revature.schoolDatabase.models.Person;
import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.screens.menus.FacultyMenu;
import com.revature.schoolDatabase.screens.menus.StudentMenu;
import com.revature.schoolDatabase.services.CourseService;
import com.revature.schoolDatabase.services.UserService;
import com.revature.schoolDatabase.util.ScreenRouter;
import com.revature.schoolDatabase.util.exceptions.AuthenticationException;
import com.revature.schoolDatabase.util.exceptions.InvalidRequestException;
import org.omg.CORBA.DynAnyPackage.Invalid;

import java.io.BufferedReader;

public class LoginScreen extends Screen {

    private final UserService userService;
    private final CourseService courseService;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    public LoginScreen(BufferedReader consoleReader, ScreenRouter router, UserService userService, CourseService courseService) {
        super("LoginScreen", "/login", consoleReader, router);
        this.userService = userService;
        this.courseService = courseService;
    }

    @Override
    public void render() throws Exception {

        System.out.println("\nLogin to your account!");

        System.out.print("Username: ");
        String username = consoleReader.readLine();

        System.out.print("Password: ");
        String password = consoleReader.readLine();

        // TODO follow to UserService -> expand later
        Person newPerson;
        try {
            newPerson = userService.login(username, password);
        } catch (InvalidRequestException | AuthenticationException ae) {
            System.out.println(ANSI_RED + "ERROR: Invalid credentials" + ANSI_RESET);
            System.out.println("Taking you back to main menu...");
            router.navigate("/main");
            return;
        }
        if (newPerson == null) {
            System.out.println("No user found, taking you back to main menu...");
            router.navigate("/main");
            return;
        }

        switch (newPerson.getUserType()) {
            case "faculty":
                System.out.println("Logging in as faculty...");
                // Add FacultyMenu to HashSet
                router.addScreen(new FacultyMenu((Faculty) newPerson, consoleReader, router, userService, courseService));
                router.navigate("/faculty");
                break;
            case "student":
                System.out.println("Logging in as student...");
                // Add StudentMenu to HashSet
                router.addScreen(new StudentMenu((Student) newPerson, consoleReader, router, userService, courseService));
                router.navigate("/student");
                break;
            default:
                System.out.println("No user found, taking you back to main menu...");
                router.navigate("/main");
                break;
        }


    }
}
