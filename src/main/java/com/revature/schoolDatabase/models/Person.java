package com.revature.schoolDatabase.models;

public abstract class Person {
    // Variables
    protected int id;
    protected String firstName;
    protected String lastName;
    protected String username;
    protected String password;

    // Constructors
    public Person(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }

    // Getters and Setters


    // Methods


}
