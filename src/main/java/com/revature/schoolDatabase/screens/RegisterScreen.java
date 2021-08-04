package com.revature.schoolDatabase.screens;

import com.revature.schoolDatabase.util.ScreenRouter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;

public class RegisterScreen extends Screen{

    private final Logger logger = LogManager.getLogger(RegisterScreen.class);
//    private final UserService userService;

    public RegisterScreen(BufferedReader consoleReader, ScreenRouter router) {
        super("RegisterScreen", "/register", consoleReader, router);
    }

    @Override
    public void render() throws Exception {
        System.out.println("\nRegister for a new account!");

        System.out.print("First name: ");
        String firstName = consoleReader.readLine();

        System.out.print("Last name: ");
        String lastName = consoleReader.readLine();

        System.out.print("Email: ");
        String email = consoleReader.readLine();

        System.out.print("Username: ");
        String username = consoleReader.readLine();

        System.out.print("Password: ");
        String password = consoleReader.readLine();

//        AppUser newUser = new AppUser(firstName, lastName, email, username, password);

        try {
//            userService.register(newUser);
            logger.info("User successfully registered!");
            System.out.println("User successfully registered!");
            router.navigate("/main");
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.debug("User not registered!");
            router.navigate("/main");
        }



    }
}
