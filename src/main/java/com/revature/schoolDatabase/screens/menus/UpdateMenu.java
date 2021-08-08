package com.revature.schoolDatabase.screens.menus;

import com.revature.schoolDatabase.models.Course;
import com.revature.schoolDatabase.services.CourseService;
import com.revature.schoolDatabase.services.UserService;
import com.revature.schoolDatabase.util.ScreenRouter;

import java.io.BufferedReader;

public class UpdateMenu<T> extends Menu{
    // Variables
    private T object;
    private final UserService userService;
    private final CourseService courseService;

    // Constructors
    public UpdateMenu(T object, String name, String route, BufferedReader consoleReader, ScreenRouter router,
                      UserService userService, CourseService courseService, String[] menuOptions) {
        super(name, route, consoleReader, router, menuOptions);
        this.object = object;
        this.userService = userService;
        this.courseService = courseService;
    }

    @Override
    public void render() throws Exception {
        // Display Object Info
        if (object.getClass().equals(Course.class)) {
            Course course = (Course) object;
            course.displayCourse();
            displayMenu();

            // Split user input on whitespace
            String[] userSelection = consoleReader.readLine().split(" ");
            System.out.println();

            // TODO Finish parsing
            switch (userSelection[0]) {
                case "1":   // Change Title
                    course.setTitle(userSelection[1]);
                    courseService.updateCourse(course);
                    courseService.findCourseByID(course.getDeptShort(), course.getCourseNo(), course.getSectionNo()).displayCourse();
                    break;
                default:
                    System.out.println("Invalid selection");
                    System.out.println("Taking you back to FacultyMenu...");
                    router.navigate("/faculty");
                    return;
            }
        }
        else {
            System.out.println("It's not a course!");
            System.out.println("Taking you back to FacultyMenu...");
            router.navigate("/faculty");
        }

    }

}
