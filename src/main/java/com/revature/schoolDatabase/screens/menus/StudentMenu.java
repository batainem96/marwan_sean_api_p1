package com.revature.schoolDatabase.screens.menus;

import com.revature.schoolDatabase.models.Course;
import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.screens.Screen;
import com.revature.schoolDatabase.services.CourseService;
import com.revature.schoolDatabase.services.UserService;
import com.revature.schoolDatabase.util.ScreenRouter;

import java.io.BufferedReader;

public class StudentMenu extends Menu {
    // Variables
    private final Student stud;
    private final UserService userService;
    private final CourseService courseService;

    // Constructors
    public StudentMenu(Student stud, BufferedReader consoleReader, ScreenRouter router, UserService userService, CourseService courseService) {
        super("Student", "/student", consoleReader, router, new String[] {"View My Courses", "View Available Courses", "View All Courses", "Add Course <CourseID>", "Remove Course", "Cancel Registration"});
        this.stud = stud;
        this.userService = userService;
        this.courseService = courseService;
    }

    // Methods
    @Override
    public void render() throws Exception {
        displayMenu();

        // Split user input on whitespace
        String[] userSelection = consoleReader.readLine().split(" ");
        System.out.println();

        switch (userSelection[0]) {
            case "1":   // View My Courses
                courseService.showCourses();
                break;
            case "2":   // View Available Courses
                courseService.showCourses();
                break;
            case "3":   // View All Courses
                courseService.showCourses();
                break;
            case "4":   // User wants to add a course, check if they gave a course ID, otherwise show usage
                if (userSelection.length > 1) {
                    courseService.addCourse(stud, userSelection[1].toUpperCase(), Integer.parseInt(userSelection[2]));
                }
                else System.out.println("Usage: 4 <DeptShorthand> <CourseID>\nExample: 4 COSC 101001");
                break;
            case "5":   // Remove Course
                break;
            case "6":   // Cancel Registration
                break;
            default:
                System.out.println("Taking you back to main menu...");

                // Remove StudentMenu from HashSet
                if (router.getScreens().contains(this)) {
                    router.removeScreen(this);
                }

                router.navigate("/main");
                break;
        }


    }
}
