package com.revature.portal.datasource.models;

public class Faculty extends User {
    private String department;

    public Faculty() {
        super("Faculty");
    }

    public Faculty(String firstName, String lastName, String email, String username, String password) {
        super(firstName, lastName, email, username, password, "faculty");
    }

    // Overloaded constructor with id passed
    public Faculty(String id, String firstName, String lastName, String email, String username, String password) {
        super(id, firstName, lastName, email, username, password, "faculty");
    }
}
