package com.revature.schoolDatabase.screens.menus;

import com.revature.schoolDatabase.util.ScreenRouter;

import java.io.BufferedReader;

public class UpdateMenu extends Menu{
    public UpdateMenu(String name, String route, BufferedReader consoleReader, ScreenRouter router, String[] menuOptions) {
        super(name, route, consoleReader, router, menuOptions);
    }
}
