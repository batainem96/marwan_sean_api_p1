package com.revature.schoolDatabase.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MeetingTime {
    @JsonProperty("day")
    private String day;
    @JsonProperty("startTime")
    private int startTime;
    @JsonProperty("endTime")
    private int endTime;
    @JsonProperty("classType")
    private String classType;

    public MeetingTime() {}

    public MeetingTime(String day, int startTime, int endTime, String classType) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classType = classType;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
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

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeetingTime that = (MeetingTime) o;
        return startTime == that.startTime && endTime == that.endTime && Objects.equals(day, that.day) && Objects.equals(classType, that.classType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, startTime, endTime, classType);
    }

    @Override
    public String toString() {
        return "MeetingTime{" +
                "day='" + day + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", classType='" + classType + '\'' +
                '}';
    }
}
