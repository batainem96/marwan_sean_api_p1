package com.revature.schoolDatabase.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Field;
import java.util.*;

import static com.revature.schoolDatabase.models.DeptShorthand.deptToShort;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Course {
    // Variables
    private String id;
    private String title;
    private String department;
    private String deptShort;
    private int courseNo;
    private int sectionNo;
    @JsonProperty("prerequisites")
    private ArrayList<PreReq> prerequisites = new ArrayList<>();
    private String instructor;
    private int credits;
    private int totalSeats;
    private int openSeats;
    @JsonProperty("meetingTimes")
    private ArrayList<MeetingTime> meetingTimes = new ArrayList<>();
    private String description;

    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RESET = "\u001B[0m";

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
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public int getCourseNo() {
        return courseNo;
    }

    public void setCourseNo(int courseNo) {
        this.courseNo = courseNo;
    }

    public int getSectionNo() {
        return sectionNo;
    }

    public void setSectionNo(int sectionNo) {
        this.sectionNo = sectionNo;
    }

    public ArrayList<PreReq> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(ArrayList<PreReq> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
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

    public ArrayList<MeetingTime> getMeetingTimes() {
        return meetingTimes;
    }

    public void setMeetingTimes(ArrayList<MeetingTime> meetingTimes) {
        this.meetingTimes = meetingTimes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return courseNo == course.courseNo && sectionNo == course.sectionNo && credits == course.credits && totalSeats == course.totalSeats && openSeats == course.openSeats && Objects.equals(id, course.id) && Objects.equals(title, course.title) && Objects.equals(department, course.department) && Objects.equals(deptShort, course.deptShort) && Objects.equals(prerequisites, course.prerequisites) && Objects.equals(instructor, course.instructor) && Objects.equals(meetingTimes, course.meetingTimes) && Objects.equals(description, course.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, department, deptShort, courseNo, sectionNo, prerequisites, instructor, credits, totalSeats, openSeats, meetingTimes, description);
    }

    // Methods
    /**
     * Displays course information in an easily readable format
     */
    public void displayCourse() {
        try {
            System.out.println(ANSI_CYAN + this.title + ANSI_RESET);
            System.out.println(this.deptShort + " " + this.courseNo + "-" + this.sectionNo);
            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.getName().equals("id") ||
                        field.getName().equals("title") ||
                        field.getName().equals("deptShort") ||
                        field.getName().equals("prerequisites") ||
                        field.getName().equals("meetingTimes") ||
                        field.getName().equals("ANSI_CYAN") ||
                        field.getName().equals("ANSI_RESET") ||
                        field.get(this) == null)
                    continue;
                System.out.println(field.getName() + ": " + field.get(this));
            }
            this.displayMeetingTimes();
            System.out.println();
            this.displayPrerequisites();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays minimal course information
     */
    public void displayShortCourse() {
        try {
            System.out.println(ANSI_CYAN + this.title + ANSI_RESET);
            System.out.println(this.deptShort + " " + this.courseNo + "-" + this.sectionNo);
        } catch (Exception e) { // TODO Handle exceptions
            e.printStackTrace();
        }
    }

    /**
     * Displays meeting times of Course if available
     */
    public void displayMeetingTimes() {
        if (meetingTimes.isEmpty())
            return;
        else {
            System.out.println("Meeting Times:");
            for (MeetingTime meetingTime : meetingTimes) {
                System.out.print("\t" + meetingTime.getDay());
                System.out.print(" " + meetingTime.getStartTime());
                System.out.print(" - " + meetingTime.getEndTime());
                System.out.println(" (" + meetingTime.getClassType() + ")");
            }
        }
    }

    /**
     * Displays prerequisites of Course if available
     */
    public void displayPrerequisites() {
        if (prerequisites.isEmpty())
            return;
        else {
            System.out.println("Prerequisites: ");
            for (PreReq prereq : prerequisites) {
                System.out.print("\t" + prereq.getDepartment());
                System.out.print(" " + prereq.getCourseNo());
                System.out.println(" (" + prereq.getCredits() + " credits)");
            }
        }
    }

}
