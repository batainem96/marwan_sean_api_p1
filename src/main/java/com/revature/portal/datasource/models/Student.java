package com.revature.portal.datasource.models;

public class Student extends User {
    public Student() {
        super("student");
    }

    public Student(String firstName, String lastName, String email, String username, String password) {
        super(firstName, lastName, email, username, password, "student");
    }

    // Overloaded constructor with id passed
    public Student(String id, String firstName, String lastName, String email, String username, String password) {
        super(id, firstName, lastName, email, username, password, "student");
    }
}
