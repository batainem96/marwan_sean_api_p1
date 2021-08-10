package com.revature.schoolDatabase.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Person {
    // Variables
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String username;
    protected String password;
    protected String userType;
    protected ArrayList<Schedule> schedule = new ArrayList<>();

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BRIGHT_BLUE = "\u001B[94m";

    // Constructors
    public Person() {
        super();
    }

    public Person(String firstName, String lastName, String username, String password, String userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    // Overloaded constructor with id passed
    public Person(String id, String firstName, String lastName, String username, String password, String userType) {
        this(firstName, lastName, username, password, userType);
        this.id = id;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public ArrayList<Schedule> getSchedule() {
        return schedule;
    }

    public void setSchedule(ArrayList<Schedule> schedule) {
        this.schedule = schedule;
    }

    // Overridden methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName) && Objects.equals(username, person.username) && Objects.equals(password, person.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, username, password);
    }

    // Other Methods
    public void displayUser() {
        System.out.println(ANSI_YELLOW + "\tUSER: " + this.firstName + " " + this.lastName + " (" + this.getUserType() + ")" + ANSI_RESET);
        System.out.println("\t\tUsername: " + this.username);
//        this.displaySchedule();
    }

    public void displaySchedule() {
        if (schedule.isEmpty())
            return;
        else {
            System.out.println(ANSI_BRIGHT_BLUE + "Schedule:" + ANSI_RESET);
            for (Schedule course : schedule) {
                System.out.print("\t" + course.getCourseDept());
                System.out.print(" " + course.getCourseNo());
                System.out.println("-" + course.getSectionNo());
                System.out.print("\t");
                course.displayMeetingTimes();
            }
        }
    }


}
