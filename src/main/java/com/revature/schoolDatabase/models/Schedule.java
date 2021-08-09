package com.revature.schoolDatabase.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Schedule {
    @JsonProperty("courseDept")
    private String courseDept;
    @JsonProperty("courseNo")
    private int courseNo;
    @JsonProperty("sectionNo")
    private int sectionNo;
    @JsonProperty("meetingTimes")
    private ArrayList<MeetingTime> meetingTimes = new ArrayList<>();

    public Schedule() {

    }

    public Schedule(String courseDept, int courseNo, int sectionNo, ArrayList<MeetingTime> meetingTimes) {
        this.courseDept = courseDept;
        this.courseNo = courseNo;
        this.sectionNo = sectionNo;
        this.meetingTimes = meetingTimes;
    }

    public String getCourseDept() {
        return courseDept;
    }

    public void setCourseDept(String courseDept) {
        this.courseDept = courseDept;
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

    public ArrayList<MeetingTime> getMeetingTimes() {
        return meetingTimes;
    }

    public void setMeetingTimes(ArrayList<MeetingTime> meetingTimes) {
        this.meetingTimes = meetingTimes;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "courseDept='" + courseDept + '\'' +
                ", courseNo=" + courseNo +
                ", sectionNo=" + sectionNo +
                ", meetingTimes=" + meetingTimes +
                '}';
    }
}
