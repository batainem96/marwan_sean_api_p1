package com.revature.schoolDatabase.models;

public class Student extends Person {
    public Student() {
        super();
    }

    public Student(String firstName, String lastName, String username, String password) {
        super(firstName, lastName, username, password, "student");
    }
}
