package com.revature.schoolDatabase.models;

public class Student extends Person {
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
