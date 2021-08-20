package com.revature.schoolDatabase.datasource.models;

public class Student extends User {
    public Student() {
        super();
    }

    public Student(String firstName, String lastName, String username, String password) {
        super(firstName, lastName, username, password, "student");
    }

    // Overloaded constructor with id passed
    public Student(String id, String firstName, String lastName, String username, String password) {
        super(id, firstName, lastName, username, password, "student");
    }
}
