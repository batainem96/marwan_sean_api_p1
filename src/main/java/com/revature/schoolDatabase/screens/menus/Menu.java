package com.revature.schoolDatabase.screens.menus;

import com.revature.schoolDatabase.screens.Screen;
import com.revature.schoolDatabase.util.ScreenRouter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;

import static com.revature.schoolDatabase.util.ProgramManager.*;

public abstract class Menu extends Screen {
    // Variables
    protected String name;
    protected String[] menuOptions = {"This menu is empty!"};
    private final Logger logger = LogManager.getLogger(Menu.class);


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
    }


}
