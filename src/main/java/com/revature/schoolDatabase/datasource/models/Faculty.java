package com.revature.schoolDatabase.datasource.models;

public class Faculty extends User {
    private String department;

    public Faculty() {
        super();
    }

    public Faculty(String firstName, String lastName, String username, String password) {
        super(firstName, lastName, username, password, "faculty");
    }

    // Overloaded constructor with id passed
    public Faculty(String id, String firstName, String lastName, String username, String password) {
        super(id, firstName, lastName, username, password, "faculty");
    }
}
