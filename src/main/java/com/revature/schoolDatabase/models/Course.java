package com.revature.schoolDatabase.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.lang.reflect.Field;
import java.sql.SQLOutput;
import java.util.*;

import static com.revature.schoolDatabase.models.DeptShorthand.deptToShort;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Course {
    // Variables
    @JsonProperty("_id")
    private String _id;
    private String title;
    private String department;
    private String deptShort;
    private int courseNo;
    private int sectionNo;
    @JsonProperty("prerequisites")
    private ArrayList<PreReq> prerequisites = new ArrayList<>();
    private String instructor;
    @JsonProperty("meetingTimes")
    private ArrayList<MeetingTime> meetingTimes = new ArrayList<>();
    private int totalSeats;
    private int openSeats;

    // Constructors
    public Course() {}

    public Course(String title, String department, int courseNo, int sectionNo) {
        this.title = title;
        this.department = department;
        this.courseNo = courseNo;
        this.sectionNo = sectionNo;
        this.deptShort = deptToShort.get(department);
        this.instructor = "None";
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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
        try {
            System.out.println(this.title);
            System.out.println(this.deptShort + " " + this.courseNo + "-" + this.sectionNo);
            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.getName().equals("_id") ||
                        field.getName().equals("prerequisites") ||
                        field.getName().equals("meetingTimes"))
                    continue;
                System.out.println(field.getName() + ": " + field.get(this));
            }
            this.displayMeetingTimes();
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays meeting times of Course if available
     *
     */
    public void displayMeetingTimes() {
        if (meetingTimes.isEmpty())
            return;
        else {
            System.out.println("Meeting Times:");
            for (MeetingTime meetingTime : meetingTimes) {
                System.out.println("\t" + meetingTime.getDay());
                System.out.println("\t" + meetingTime.getStartTime());
                System.out.println("\t" + meetingTime.getEndTime());
                System.out.println("\t" + meetingTime.getClassType());
            }
        }
    }

}
