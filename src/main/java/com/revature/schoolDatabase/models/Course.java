package com.revature.schoolDatabase.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Course {
    // Variables
    private int id;
    private String title;
    private String department;
    private String deptShort;
    private int courseNo;
    private int sectionNo;
//    private String[] prerequisites;
    private String instructor;
    private int startTime;
    private int endTime;
    private String[] days;
    private int totalSeats;
    private int openSeats;

    // Constructors
    public Course() {

    }

    public Course(int id, String title, String department) {
        this.id = id;
        this.title = title;
        this.department = department;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDeptShort() {
        return deptShort;
    }

    public void setDeptShort(String deptShort) {
        this.deptShort = deptShort;
    }

    public int getSectionNo() {
        return sectionNo;
    }

    public void setSectionNo(int sectionNo) {
        this.sectionNo = sectionNo;
    }

    public int getCourseNo() {
        return courseNo;
    }

    public void setCourseNo(int courseNo) {
        this.courseNo = courseNo;
    }

//    public String[] getPrerequisites() {
//        return prerequisites;
//    }
//
//    public void setPrerequisites(String[] prerequisites) {
//        this.prerequisites = prerequisites;
//    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String[] getDays() {
        return days;
    }

    public void setDays(String[] days) {
        this.days = days;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getOpenSeats() {
        return openSeats;
    }

    public void setOpenSeats(int openSeats) {
        this.openSeats = openSeats;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Course course = (Course) o;
//        return id == course.id && startTime == course.startTime && endTime == course.endTime && totalSeats == course.totalSeats && openSeats == course.openSeats && Objects.equals(title, course.title) && Objects.equals(department, course.department) && Arrays.equals(prerequisites, course.prerequisites) && Arrays.equals(days, course.days);
//    }

//    @Override
//    public int hashCode() {
//        int result = Objects.hash(id, title, department, startTime, endTime, totalSeats, openSeats);
//        result = 31 * result + Arrays.hashCode(prerequisites);
//        result = 31 * result + Arrays.hashCode(days);
//        return result;
//    }

    // Methods
    /**
     * Displays course information in an easily readable format
     */
    public void displayCourse() {
        System.out.println(this.title);
        System.out.println(this.deptShort + " " + this.courseNo + "-" + this.sectionNo);
    }

}
