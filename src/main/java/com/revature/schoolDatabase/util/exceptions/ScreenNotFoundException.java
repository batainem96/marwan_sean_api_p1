package com.revature.schoolDatabase.util.exceptions;

public class ScreenNotFoundException extends RuntimeException {

    public ScreenNotFoundException() {
        super("No screen found with provided route!");
    }

}
