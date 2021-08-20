package com.revature.schoolDatabase.web.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.revature.schoolDatabase.datasource.models.CourseHeader;

import java.util.ArrayList;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class PersonDTO {
    // Variables
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String username;
    protected String userType;
    protected ArrayList<CourseHeader> schedule = new ArrayList<>();

    // Constructors
    public PersonDTO() {
        super();
    }

    public PersonDTO(String firstName, String lastName, String username, String userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.userType = userType;
    }

    // Overloaded constructor with id passed
    public PersonDTO(String id, String firstName, String lastName, String username, String userType) {
        this(firstName, lastName, username, userType);
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
        PersonDTO personDTO = (PersonDTO) o;
        return Objects.equals(id, personDTO.id) && Objects.equals(firstName, personDTO.firstName) && Objects.equals(lastName, personDTO.lastName) && Objects.equals(username, personDTO.username) && Objects.equals(userType, personDTO.userType) && Objects.equals(schedule, personDTO.schedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, username, userType, schedule);
    }

    @Override
    public String toString() {
        return "PersonDTO{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", userType='" + userType + '\'' +
                ", schedule=" + schedule +
                '}';
    }
}
