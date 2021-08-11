package com.revature.schoolDatabase.screens.menus;

import com.revature.schoolDatabase.models.Course;
import com.revature.schoolDatabase.models.Schedule;
import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.screens.Screen;
import com.revature.schoolDatabase.services.CourseService;
import com.revature.schoolDatabase.services.UserService;
import com.revature.schoolDatabase.util.ScreenRouter;
import com.revature.schoolDatabase.util.exceptions.SchedulingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;

public class StudentMenu extends Menu {
    // Variables
    private Student stud;
    private final UserService userService;
    private final CourseService courseService;
    private static final String[] menuOptions = {"View My Courses", "View Available Courses", "View All Courses",
            "Add Course <CourseID>", "Remove Course", "Cancel Registration", "Log Out and Return to Main Menu"};

    private final Logger logger = LogManager.getLogger(StudentMenu.class);

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    // Constructors
    public StudentMenu(Student stud, BufferedReader consoleReader, ScreenRouter router, UserService userService, CourseService courseService) {
        super("Student", "/student", consoleReader, router, menuOptions);
        this.stud = stud;
        this.userService = userService;
        this.courseService = courseService;
    }

    // Methods


    @Override
    public void render() throws Exception {
        System.out.println();
        stud.displayUser();
        displayMenu();

        // Split user input on whitespace
        String[] userSelection = consoleReader.readLine().split(" ");
        System.out.println();

        try {
            switch (userSelection[0]) {
                case "1":   // View My Courses
                    stud.displaySchedule();
                    break;
                case "2":   // View Available Courses
                    courseService.showCourses(stud, "open");
                    break;
                case "3":   // View All Courses
                    courseService.showCourses();
                    break;
                case "4":   // User wants to add a course, check if they gave a course ID, otherwise show usage
                    if (userSelection.length > 1) {
                        try {
                            stud = (Student) courseService.addCourse(stud, userSelection[1].toUpperCase(), userSelection[2]);
                            // Update student
                            userService.updateUser(stud);
                        } catch (SchedulingException se) {
                            logger.error(se.getMessage());
                            logger.debug("ERROR: Class has no open seats!");
                            System.out.println(ANSI_RED + "ERROR: Class has no open seats!" + ANSI_RESET);
                        }
                    } else System.out.println("Usage: 4 <DeptShorthand> <CourseID>\nExample: 4 COSC 101-1");
                    break;
                case "5":   // Remove Course
                    if (userSelection.length > 1) {
                        stud = (Student) courseService.removeCourseFromSchedule(stud, new Schedule(userSelection[1].toUpperCase(), userSelection[2]));
                        // Update student
                        userService.updateUser(stud);
                    } else System.out.println("Usage: 5 <DeptShorthand> <CourseID>\nExample: 5 COSC 101-1");
                    break;
                case "6":   // Cancel Registration // TODO Confirm Screen
                    userService.deleteUser(stud);
                case "7":   // Log Out and Return to Main Menu
                    System.out.println("Taking you back to main menu...");

                    // Remove StudentMenu from HashSet
                    if (router.getScreens().contains(this))
                        router.removeScreen(this);

                    router.navigate("/main");
                    break;
                default:    // Invalid Selection
                    System.out.println("Invalid input, please try again");
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException aie) {
            logger.error(aie.getMessage());
            logger.debug("ERROR: Invalid criteria, try again.");
            System.out.println(ANSI_RED + "ERROR: Invalid criteria, try again." + ANSI_RESET);
        }


    }
}
