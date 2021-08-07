package com.revature.schoolDatabase.services;

import com.revature.schoolDatabase.models.Course;
import com.revature.schoolDatabase.models.Faculty;
import com.revature.schoolDatabase.models.Person;
import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.repositories.CourseRepository;

import java.util.List;

public class CourseService {
    // Variables
    private final CourseRepository courseRepo;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public CourseService(CourseRepository courseRepo) {
        this.courseRepo = courseRepo;
    }

    /**
     * Lists all courses in catalog
     */
    public void showCourses() {
        System.out.println(ANSI_CYAN + "Title: Introduction to Programming\n" + ANSI_RESET +
                "Department: COSC\n" +
                "Course ID: 2784\n");

        // TODO List all courses currently stored in database
        List<Course> courseList = courseRepo.retrieveCourses();
        for (Course course : courseList) {
            course.displayCourse();
        }
    }

    /**
     * Lists courses in catalog according to flags
     *
     * @param flags -- used to specify which courses to display
     *             List of flags:
     *                  -- 'open' = courses with open seats
     *                  -- 'closed' = courses which have no open seats
     *                  -- 'user' = courses currently in a given Person's schedule
     *                  -- 'schedule' = courses that would fit in schedule
     *                  -- 'dept' = courses in a given Department
     */
    public void showCourses(Person user, String... flags) {
        System.out.println(ANSI_CYAN + "Title: Introduction to Programming\n" + ANSI_RESET +
                "Department: COSC\n" +
                "Course ID: 2784\n");
    }

    /**
     * Adds a new course to a Student's schedule
     *
     * @param stud
     * @param courseID
     */
    public void addCourse(Student stud, String dept, String courseID) {
        // TODO -------------------------------

        // Find course in database given courseID
        String[] splitID = courseID.split("-");
        Course newCourse = courseRepo.findById(dept, Integer.parseInt(splitID[0]), Integer.parseInt(splitID[1]));

        newCourse.displayCourse();
        // TODO Compare new course info with student (schedule, etc) to ensure the add is valid

        // Add new course to student's schedule

    }

    /**
     * Creates a new course, given user is a Faculty member
     *
     * @param fac
     * @param newCourse
     */
    public void createCourse(Faculty fac, Course newCourse) {
        // TODO -------------------------------
        // Verify faculty is in database and is qualified to create a course

        // Verify that the new course does not already exist in the database

        // Store course in database with given faculty as "professor"
    }
}
