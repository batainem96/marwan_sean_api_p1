package com.revature.schoolDatabase.web.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.revature.schoolDatabase.datasource.models.CourseHeader;
import com.revature.schoolDatabase.datasource.models.User;

import java.util.ArrayList;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    // Variables
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String username;
    protected String userType;
    protected ArrayList<CourseHeader> schedule = new ArrayList<>();

    // Constructors
    public UserDTO() {
        super();
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUsername();
        this.userType = user.getUserType();
        this.schedule = user.getSchedule();
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

    // Overridden methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id) && Objects.equals(firstName, userDTO.firstName) && Objects.equals(lastName, userDTO.lastName) && Objects.equals(username, userDTO.username) && Objects.equals(userType, userDTO.userType) && Objects.equals(schedule, userDTO.schedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, username, userType, schedule);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", userType='" + userType + '\'' +
                ", schedule=" + schedule +
                '}';
    }
}
