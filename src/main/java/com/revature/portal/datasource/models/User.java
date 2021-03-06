package com.revature.portal.datasource.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.revature.portal.web.dtos.CourseHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)  // Only insert fields to database if non null
public class User {
    // Variables
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String firstName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String lastName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String username;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String password;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String role;
    protected List<Course> schedule = new ArrayList<>();

    public User() {

    }

    public User(String userType) {
        this.role = userType;
    }

    public User(String firstName, String lastName, String email, String username, String password, String userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = userType;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Course> getSchedule() {
        return schedule;
    }

    public void setSchedule(ArrayList<Course> schedule) {
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
                ", userType='" + role + '\'' +
                ", schedule=" + schedule +
                '}';
    }

    public void addToSchedule(Course course) {
        this.schedule.add(course);
    }

    public boolean removeFromSchedule(Course courseHeader) {
        if (this.schedule.contains(courseHeader)) {
            this.schedule.remove(courseHeader);
            return true;
        }
        return false;
    }


}
