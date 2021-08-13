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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;

import static com.revature.schoolDatabase.util.ProgramManager.*;

public class FacultyMenu extends Menu {
    // Variables
    private Faculty fac;
    private final UserService userService;
    private final CourseService courseService;

    private final Logger logger = LogManager.getLogger(FacultyMenu.class);

    // Constructors
    public FacultyMenu(Faculty fac, BufferedReader consoleReader, ScreenRouter router,
                       UserService userService, CourseService courseService) {
        super("Faculty", "/faculty", consoleReader, router,
                        new String[] {"View My Courses", "View Available Courses", "View All Courses", "Display Users", "Create Course",
                        "Edit Course", "Remove Course", "Log Out and Return to Main Menu"});
        this.userService = userService;
        this.courseService = courseService;
        this.fac = courseService.generateSchedule(fac);
    }

    // Methods
    @Override
    public void render() throws Exception {
        System.out.println();
        System.out.println(ANSI_YELLOW + "\tCURRENT USER" + ANSI_RESET);
        fac.displayUser();
        displayMenu();

        // Split user input on whitespace or dash
        String[] userSelection = consoleReader.readLine().split("[ -]+");
        if (userSelection.length > 1)
            userSelection[1] = userSelection[1].toUpperCase();
        System.out.println();

        switch (userSelection[0]) {
            case "1":       // View My Courses
                fac = courseService.generateSchedule(fac);
                fac.displaySchedule();
                break;
            case "2":       // View Available Courses
                courseService.showCourses(fac, "open");
                break;
            case "3":       // View All Courses
                courseService.showCourses();
                break;
            case "4":       // Show List of Users
                userService.showUsers();
                break;
            case "5":       // Add Course
                // Create a new course
                Course newCourse = CourseCreator.createCourse(consoleReader);
                if (courseService.findCourseByCredentials(newCourse.getDeptShort(), newCourse.getCourseNo(), newCourse.getSectionNo()) != null)
                    System.out.println(ANSI_RED + "ERROR: Course already exists." + ANSI_RESET);
                else {
                    try {
                        courseService.createCourse(newCourse);
                    } catch (DataSourceException dse) {
                        System.out.println(ANSI_RED + "ERROR: Failed to create course." + ANSI_RESET);
                    }
                }
                break;
            case "6":       // Edit Course
                try {
                    Course editCourse = courseService.findCourseByCredentials(userSelection[1],
                            Integer.parseInt(userSelection[2]), Integer.parseInt(userSelection[3]));
                    if (editCourse == null) {
                        System.out.println(ANSI_RED + "ERROR: Could not retrieve course from database" + ANSI_RESET);
                        break;
                    }
                    String[] menuOptions = {"Title", "Department", "Course Number", "Section Number", "Instructor",
                            "Credits", "Total Number of Seats", "Prerequisites {WIP}", "Meeting Times {WIP}",
                            "Description", "Exit"};
                    UpdateMenu updateMenu = new UpdateMenu<Course>(editCourse, "Edit Course", "/update",
                            consoleReader, router, userService, courseService, menuOptions);
                    router.addScreen(updateMenu);
                    router.navigate("/update");
                } catch (ArrayIndexOutOfBoundsException aie) {
                    System.out.println(ANSI_RED + "ERROR: Incorrect criteria, try again." + ANSI_RESET);
                } catch (Exception e) {
                    System.out.println(ANSI_RED + "ERROR: Failed to update course." + ANSI_RESET);
                }
                break;
            case "7":       // Remove Course
                try {
                    boolean result = courseService.deleteCourse(userSelection[1], Integer.parseInt(userSelection[2]), Integer.parseInt(userSelection[3]));
                    // Update Schedules where applicable
                    for (Person user : userService.retrieveUsers()) {
                        Schedule oldSched = new Schedule(userSelection[1], Integer.parseInt(userSelection[2]), Integer.parseInt(userSelection[3]));
                        for (Schedule sched : user.getSchedule()) {
                            if (sched.equals(oldSched)) {
                                user = courseService.removeCourseFromSchedule(user, sched);
                                userService.updateUser(user);
                                break;
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException aie) {
                    logger.error(aie.getMessage());
                    System.out.println(ANSI_RED + "ERROR: Incorrect criteria, try again." + ANSI_RESET);
                }
                catch (Exception e) {
                    logger.error(e.getMessage());
                    System.out.println(ANSI_RED + "ERROR: Failed to delete course." + ANSI_RESET);
                }
                break;
            case "8":
                System.out.println("Taking you back to main menu...");

                // Remove FacultyMenu from HashSet
                if (router.getScreens().contains(this))
                    router.removeScreen(this);

                router.navigate("/main");
                break;
            default:
                System.out.println("Invalid selection, please try again.");
                break;
        }
    }
}
