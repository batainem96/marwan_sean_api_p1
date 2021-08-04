package com.revature.schoolDatabase.screens.menus;

import com.revature.schoolDatabase.models.Course;
import com.revature.schoolDatabase.models.Faculty;
import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.services.UserService;
import com.revature.schoolDatabase.util.ScreenRouter;

import java.io.BufferedReader;

public class FacultyMenu extends Menu {
    // Variables
    private final UserService userService;

    // Constructors
    public FacultyMenu(BufferedReader consoleReader, ScreenRouter router, UserService userService) {
        super("Faculty", "/faculty", consoleReader, router, new String[] {"View My Courses", "View Available Courses", "View All Courses", "Add Course", "Edit Course", "Remove Course"});
        this.userService = userService;
    }

    // Methods
    @Override
    public void render() throws Exception {
        displayMenu();

        String userSelection = consoleReader.readLine();

        switch (userSelection) {
            case "1":
                userService.showCourses();
                break;
            case "2":
                userService.showCourses();
                break;
            case "3":
                userService.showCourses();
                break;
            case "4":
                userService.createCourse(new Faculty("test", "test", "test", "test"), new Course() );
                break;
            default:
                System.out.println("Taking you back to main menu...");
                router.navigate("/main");
                break;
        }
    }
}
