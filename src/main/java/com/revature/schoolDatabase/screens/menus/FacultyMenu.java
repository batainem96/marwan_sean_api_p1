package com.revature.schoolDatabase.screens.menus;

import com.revature.schoolDatabase.models.Course;
import com.revature.schoolDatabase.models.Faculty;
import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.services.CourseService;
import com.revature.schoolDatabase.services.UserService;
import com.revature.schoolDatabase.util.ScreenRouter;

import java.io.BufferedReader;
import java.io.IOException;

public class FacultyMenu extends Menu {
    // Variables
    private final Faculty fac;
    private final UserService userService;
    private final CourseService courseService;

    // Constructors
    public FacultyMenu(Faculty fac, BufferedReader consoleReader, ScreenRouter router,
                       UserService userService, CourseService courseService) {
        super("Faculty", "/faculty", consoleReader, router,
                        new String[] {"View My Courses", "View Available Courses", "View All Courses", "Add Course",
                        "Edit Course", "Remove Course"});
        this.fac = fac;
        this.userService = userService;
        this.courseService = courseService;
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

        Course newCourse = new Course(title, dept, id, id);

        return newCourse;
    }

    @Override
    public void render() throws Exception {
        displayMenu();

        // Split user input on whitespace
        String[] userSelection = consoleReader.readLine().split(" ");
        System.out.println();

        // TODO Finish implementations of Faculty functions
        switch (userSelection[0]) {
            case "1":       // View My Courses
                courseService.showCourses();
                break;
            case "2":       // View Available Courses
                courseService.showCourses();
                break;
            case "3":       // View All Courses
                courseService.showCourses();
                break;
            case "4":       // Add Course
                // Create a new course
                // Prompt user for course information
                Course newCourse = createCourse();
                courseService.createCourse(fac, newCourse);
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
                courseService.deleteCourse(userSelection[1], Integer.parseInt(userSelection[2]), Integer.parseInt(userSelection[3]));
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
