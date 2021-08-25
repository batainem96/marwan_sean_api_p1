package com.revature.schoolDatabase.datasource.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.revature.schoolDatabase.web.dtos.CourseHeader;

import java.util.ArrayList;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class User {
    // Variables
    @JsonInclude(JsonInclude.Include.NON_NULL)  // Only insert ID to database if non null
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String username;
    protected String password;
    protected String userType;
    protected ArrayList<CourseHeader> schedule = new ArrayList<>();

    public User() {
        super();
    }

    public User(String userType) {
        this.userType = userType;
    }

    public User(String firstName, String lastName, String email, String username, String password, String userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    public User(String id, String firstName, String lastName, String email, String username, String password, String userType) {
        this(firstName, lastName, email, username, password, userType);
        this.id = id;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public ArrayList<CourseHeader> getSchedule() {
        return schedule;
    }

    public void setSchedule(ArrayList<CourseHeader> schedule) {
        this.schedule = schedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(username, user.username) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, username, password);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", userType='" + userType + '\'' +
                ", schedule=" + schedule +
                '}';
    }

    public void addToSchedule(CourseHeader courseHeader) {
        this.schedule.add(courseHeader);
    }

    public boolean removeFromSchedule(CourseHeader courseHeader) {
        if (this.schedule.contains(courseHeader)) {
            this.schedule.remove(courseHeader);
            return true;
        }
        return false;
    }


}
