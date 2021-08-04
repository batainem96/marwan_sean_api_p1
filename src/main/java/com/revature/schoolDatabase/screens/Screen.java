package com.revature.schoolDatabase.screens;

import com.revature.schoolDatabase.util.ScreenRouter;

import java.io.BufferedReader;
import java.util.Objects;

public abstract class Screen {
    // Variables
    protected String name;
    protected String route;
    protected BufferedReader consoleReader;
    protected ScreenRouter router;

    // Constructors
    public Screen(String name, String route, BufferedReader consoleReader, ScreenRouter router) {
        this.name = name;
        this.route = route;
        this.consoleReader = consoleReader;
        this.router = router;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public String getRoute() {
        return route;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Screen screen = (Screen) o;
        return Objects.equals(name, screen.name) && Objects.equals(route, screen.route) && Objects.equals(consoleReader, screen.consoleReader) && Objects.equals(router, screen.router);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, route, consoleReader, router);
    }

    public abstract void render() throws Exception;

}