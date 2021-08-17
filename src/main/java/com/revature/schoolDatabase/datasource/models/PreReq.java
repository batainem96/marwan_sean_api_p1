package com.revature.schoolDatabase.datasource.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PreReq {
    @JsonProperty("department")
    private String department;
    @JsonProperty("courseNo")
    private int courseNo;
    @JsonProperty("credits")
    private int credits;

    public PreReq() {
    }

    public PreReq(String department, int courseNo, int credits) {
        this.department = department;
        this.courseNo = courseNo;
        this.credits = credits;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getCourseNo() {
        return courseNo;
    }

    public void setCourseNo(int courseNo) {
        this.courseNo = courseNo;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    @Override
    public String toString() {
        return "PreReq{" +
                "department='" + department + '\'' +
                ", courseNo=" + courseNo +
                ", credits=" + credits +
                '}';
    }
}
