package com.revature.schoolDatabase.models;

public class Faculty extends Person {
    private String department;

    public Faculty(String firstName, String lastName, String username, String password) {
        super(firstName, lastName, username, password);
    }
}
