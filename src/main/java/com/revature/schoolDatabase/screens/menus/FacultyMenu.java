package com.revature.schoolDatabase.screens.menus;

import com.revature.schoolDatabase.models.Course;
import com.revature.schoolDatabase.models.Faculty;
import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.services.UserService;
import com.revature.schoolDatabase.util.ScreenRouter;

import java.io.BufferedReader;
import java.io.IOException;

public class FacultyMenu extends Menu {
    // Variables
    private final Faculty fac;
    private final UserService userService;

    // Constructors
    public FacultyMenu(Faculty fac, BufferedReader consoleReader, ScreenRouter router, UserService userService) {
        super("Faculty", "/faculty", consoleReader, router, new String[] {"View My Courses", "View Available Courses", "View All Courses", "Add Course", "Edit Course", "Remove Course"});
        this.fac = fac;
        this.userService = userService;
    }

    // Methods

    /**
     * Creates a new course for the catalog given course information
     *
     * @return
     * @throws IOException
     */
    public Course createCourse() throws IOException {
        System.out.println("Create a new course!");

        System.out.println("Enter course ID");
        int id = Integer.parseInt(consoleReader.readLine());

        System.out.println("Enter course title");
        String title = consoleReader.readLine();

        System.out.println("Enter course department");
        String dept = consoleReader.readLine();

        Course newCourse = new Course(id, title, dept);

        return newCourse;
    }

    @Override
    public void render() throws Exception {
        displayMenu();

        // Split user input on whitespace
        String[] userSelection = consoleReader.readLine().split(" ");
        System.out.println();

        switch (userSelection[0]) {
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
                // Create a new course
                // Prompt user for course information
                Course newCourse = createCourse();
                userService.createCourse(fac, newCourse);
                break;
            default:
                System.out.println("Taking you back to main menu...");

                // Remove FacultyMenu from HashSet
                if (router.getScreens().contains(this)) {
                    router.removeScreen(this);
                }

                router.navigate("/main");
                break;
        }
    }
}
