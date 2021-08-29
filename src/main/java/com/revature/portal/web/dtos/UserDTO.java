package com.revature.portal.web.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.revature.portal.datasource.models.Course;
import com.revature.portal.datasource.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    // Variables
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String username;
    protected String role;
    protected List<Course> schedule = new ArrayList<>();

    // Constructors
    public UserDTO() {
        super();
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.role = user.getRole();
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

    // Overridden methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id) && Objects.equals(firstName, userDTO.firstName) && Objects.equals(lastName, userDTO.lastName) && Objects.equals(username, userDTO.username) && Objects.equals(role, userDTO.role) && Objects.equals(schedule, userDTO.schedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, username, role, schedule);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", userType='" + role + '\'' +
                ", schedule=" + schedule +
                '}';
    }
}
