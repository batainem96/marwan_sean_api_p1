package com.revature.schoolDatabase.services;

import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.repositories.CourseRepository;

public class CourseService {
    // Variables
    private final CourseRepository courseRepo;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public CourseService(CourseRepository courseRepo) {
        this.courseRepo = courseRepo;
    }
}
