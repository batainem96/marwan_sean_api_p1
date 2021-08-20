package com.revature.schoolDatabase.web.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.revature.schoolDatabase.datasource.models.CourseHeader;
import com.revature.schoolDatabase.datasource.models.Person;

import java.util.ArrayList;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonDTO {
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

    public PersonDTO(Person person) {
        this.id = person.getId();
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.username = person.getUsername();
        this.userType = person.getUserType();
        this.schedule = person.getSchedule();
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
