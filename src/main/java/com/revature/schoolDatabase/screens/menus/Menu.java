package com.revature.schoolDatabase.screens.menus;

import com.revature.schoolDatabase.screens.Screen;
import com.revature.schoolDatabase.util.ScreenRouter;

import java.io.BufferedReader;

public abstract class Menu extends Screen {
    // Variables
    protected String name;
    protected String[] menuOptions = {"This menu is empty!"};
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m";


    // Constructors
    public Menu(String name, String route, BufferedReader consoleReader, ScreenRouter router, String[] menuOptions) {
        super(name, route, consoleReader, router);
        this.name = name;
        this.menuOptions = menuOptions;
    }

    // Methods
    public void displayMenu() {
        System.out.println("\n" + ANSI_PURPLE + name + ANSI_RESET + "\nPlease select an option:");
        for (int i = 0; i < menuOptions.length; i++)
            System.out.println((i+1) + ") " + menuOptions[i]);
        System.out.print("> ");
    }

    @Override
    public void render() throws Exception {
        displayMenu();

        String choice = consoleReader.readLine();

        try {
            System.out.println("No exception");
        } catch (Exception e) {
            System.out.println("Exception");
        }
    }


}
