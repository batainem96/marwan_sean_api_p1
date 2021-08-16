package com.revature.schoolDatabase.datasource.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.revature.schoolDatabase.services.CourseService;

import java.lang.reflect.Field;
import java.util.*;

import static com.revature.schoolDatabase.datasource.models.DeptShorthand.deptToShort;

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
    private int openSeats = 0;
    @JsonProperty("meetingTimes")
    private ArrayList<MeetingTime> meetingTimes = new ArrayList<>();
    private String description;

//    private final Logger logger = LogManager.getLogger(CourseService.class);

    // Constructors
    public Course() {}

    // In order to be valid, a Course must at least be given a title, dept, courseNo, and sectionNo
    public Course(String title, String department, int courseNo, int sectionNo) {
        this.title = title;
        this.department = department;
        this.courseNo = courseNo;
        this.sectionNo = sectionNo;
        if (deptToShort.containsKey(department))
            this.deptShort = deptToShort.get(department);
        else if (department != null && department.length() > 3) this.deptShort = department.substring(0,4);
        else this.deptShort = null;
        this.instructor = "None";
        this.credits = 0;
        this.openSeats = 0;
    }

    public Course(String title, String department, int courseNo, int sectionNo, ArrayList<PreReq> prerequisites,
                  String instructor, int credits, int totalSeats, ArrayList<MeetingTime> meetingTimes,
                  String description) {
        this(title, department, courseNo, sectionNo);
        if (prerequisites != null)
            this.prerequisites = prerequisites;
        if (instructor != null && !instructor.equals(""))
            this.instructor = instructor;
        this.credits = credits;
        this.totalSeats = totalSeats;
        this.openSeats = totalSeats;
        if (meetingTimes != null)
            this.meetingTimes = meetingTimes;
        if (description != null && !description.equals(""))
            this.description = description;
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
        if (deptToShort.containsKey(department))
            this.deptShort = deptToShort.get(department);
        else if (department != null && department.length() > 3) this.deptShort = department.substring(0,4);
        else this.deptShort = null;
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
            System.out.println(this.title);
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
            this.displayPrerequisites();
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays minimal course information
     */
    public void displayShortCourse() {
        try {
            System.out.println(this.title);
            System.out.println(this.deptShort + " " + this.courseNo + "-" + this.sectionNo);
            System.out.println();
        } catch (Exception e) {
//            logger.error(e.getMessage());
        }
    }

    /**
     * Displays meeting times of Course if available
     */
    public void displayMeetingTimes() {
        if (meetingTimes.isEmpty() || meetingTimes == null)
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
        if (prerequisites.isEmpty() || prerequisites == null)
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
