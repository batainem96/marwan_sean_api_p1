package com.revature.schoolDatabase.screens.menus;

import com.revature.schoolDatabase.screens.Screen;
import com.revature.schoolDatabase.util.ScreenRouter;

import java.io.BufferedReader;

public abstract class Menu extends Screen {
    protected String name;
    protected String[] menuOptions;

    public Menu(String name, String route, BufferedReader consoleReader, ScreenRouter router, String[] menuOptions) {
        super(name, route, consoleReader, router);
        this.name = name;
        this.menuOptions = menuOptions;
    }

    public void displayMenu() {
        System.out.println(name);
        System.out.println("Please select an option:");
        for (int i = 0; i < menuOptions.length; i++) {
            System.out.println((i+1) + ". " + menuOptions[i]);
        }
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
