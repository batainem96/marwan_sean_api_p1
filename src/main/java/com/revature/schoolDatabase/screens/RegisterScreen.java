package com.revature.schoolDatabase.screens;

import com.revature.schoolDatabase.models.Faculty;
import com.revature.schoolDatabase.models.Person;
import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.services.UserService;
import com.revature.schoolDatabase.util.ScreenRouter;
import com.revature.schoolDatabase.util.exceptions.AuthenticationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;

public class RegisterScreen extends Screen{

    private final Logger logger = LogManager.getLogger(RegisterScreen.class);
    private final UserService userService;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    public RegisterScreen(BufferedReader consoleReader, ScreenRouter router, UserService userService) {
        super("RegisterScreen", "/register", consoleReader, router);
        this.userService = userService;
    }

    @Override
    public void render() throws Exception {
        System.out.println("\nRegister for a new account!");

        System.out.print("First name: ");
        String firstName = consoleReader.readLine();

        System.out.print("Last name: ");
        String lastName = consoleReader.readLine();

        System.out.print("Username: ");
        String username = consoleReader.readLine();

        System.out.print("Password: ");
        String password = consoleReader.readLine();

        System.out.println("Are you registering as a student or faculty?\n" +
                            "1. Student\n" +
                            "2. Faculty\n");
        int studOrFac = Integer.parseInt(consoleReader.readLine());

        Person newUser;
        switch (studOrFac) {
            case 1:
                newUser = new Student(firstName, lastName, username, password);
                break;
            case 2:
                newUser = new Faculty(firstName, lastName, username, password);
                break;
            default:
                System.out.println("Invalid criteria");
                return;
        }

        try {
            if (studOrFac == 1) {
                newUser = userService.register(newUser);
                logger.info("User successfully registered!");
                System.out.println("User successfully registered!");
            }

            if (studOrFac == 2) // TODO Faculty validation
                System.out.println("Please note that a faculty account must be admin validated, and you will not be able to access it right away.");

            router.navigate("/main");
        } catch (AuthenticationException ae) {

        } catch (Exception e) {
            System.out.println(ANSI_RED + "ERROR: Invalid input. Registration failed." + ANSI_RESET);
            logger.error(e.getMessage());
            logger.debug("User not registered!");
            router.navigate("/main");
        }



    }
}
