package com.revature.schoolDatabase.screens.menus;

import com.revature.schoolDatabase.models.Course;
import com.revature.schoolDatabase.models.Faculty;
import com.revature.schoolDatabase.models.Person;
import com.revature.schoolDatabase.models.Schedule;
import com.revature.schoolDatabase.util.CourseCreator;
import com.revature.schoolDatabase.services.CourseService;
import com.revature.schoolDatabase.services.UserService;
import com.revature.schoolDatabase.util.ScreenRouter;
import com.revature.schoolDatabase.util.exceptions.DataSourceException;

import java.io.BufferedReader;

public class FacultyMenu extends Menu {
    // Variables
    private final Faculty fac;
    private final UserService userService;
    private final CourseService courseService;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    // Constructors
    public FacultyMenu(Faculty fac, BufferedReader consoleReader, ScreenRouter router,
                       UserService userService, CourseService courseService) {
        super("Faculty", "/faculty", consoleReader, router,
                        new String[] {"View My Courses", "View Available Courses", "View All Courses", "Create Course",
                        "Edit Course", "Remove Course"});
        this.userService = userService;
        this.courseService = courseService;
        this.fac = courseService.generateSchedule(fac);
    }

    // Methods
    @Override
    public void render() throws Exception {
        displayMenu();

        // Split user input on whitespace
        String[] userSelection = consoleReader.readLine().split(" ");
        System.out.println();

        // TODO Finish implementations of Faculty functions
        switch (userSelection[0]) {
            case "1":       // View My Courses
                // TODO fac.generateSchedule()
                fac.displaySchedule();
                break;
            case "2":       // View Available Courses
                courseService.showCourses(fac, "short");
                break;
            case "3":       // View All Courses
                courseService.showCourses();
                break;
            case "4":       // Add Course
                // Create a new course
                Course newCourse = CourseCreator.createCourse(consoleReader);
                if (courseService.findCourseByCredentials(newCourse.getDeptShort(), newCourse.getCourseNo(), newCourse.getSectionNo()) != null)
                    System.out.println("Course already exists");
                else {
                    try {
                        courseService.createCourse(newCourse);
                    } catch (DataSourceException dse) {
                        System.out.println(ANSI_RED + "ERROR: Failed to create course." + ANSI_RESET);
                    }
                }
                break;
            case "5":       // Edit Course
                Course editCourse = courseService.findCourseByCredentials(userSelection[1],
                        Integer.parseInt(userSelection[2]), Integer.parseInt(userSelection[3]));
                String[] menuOptions = {"Title", "Department", "Course Number", "Section Number", "Instructor",
                                        "Credits", "Total Number of Seats", "Prerequisites {WIP}", "Meeting Times {WIP}",
                                        "Description"};
                UpdateMenu updateMenu = new UpdateMenu<Course>(editCourse, "Edit Course", "/update",
                        consoleReader, router, userService, courseService, menuOptions);
                router.addScreen(updateMenu);
                router.navigate("/update");
                break;
            case "6":       // Remove Course
                try {
                    courseService.deleteCourse(userSelection[1], Integer.parseInt(userSelection[2]), Integer.parseInt(userSelection[3]));
                    // Update Schedules where applicable
                    for (Person user : userService.retrieveUsers()) {
                        Schedule oldSched = new Schedule(userSelection[1], Integer.parseInt(userSelection[2]), Integer.parseInt(userSelection[3]));
                        for (Schedule sched : user.getSchedule()) {
                            if (sched.equals(oldSched)) {
                                user = courseService.removeCourseFromSchedule(user, sched);
                                userService.updateUser(user);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println(ANSI_RED + "ERROR: Failed to delete course." + ANSI_RESET);
                    // TODO Log to file
                    e.printStackTrace();
                }
                break;
            case "7":
                System.out.println("Taking you back to main menu...");

                // Remove FacultyMenu from HashSet
                if (router.getScreens().contains(this)) {
                    router.removeScreen(this);
                }

                router.navigate("/main");
                break;
            default:
                System.out.println("Invalid selection, please try again.");
                break;
        }
    }
}
