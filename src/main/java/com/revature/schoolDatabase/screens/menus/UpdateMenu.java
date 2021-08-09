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
            String change = "";

            // TODO Finish parsing
            switch (userSelection[0]) {
                case "1":   // Change Title
                    // Parsing to allow spacing in field
                    for (int i = 1; i < userSelection.length; i++) {
                        change += userSelection[i];
                        if (i < userSelection.length - 1)
                            change += " ";
                    }
                    course.setTitle(change);
                    courseService.updateCourse(course);
                    courseService.findCourseByID(course.getId()).displayCourse();
                    break;
                case "2":   // Change Dept // TODO Auto change deptShort as well
                    // Parsing to allow spacing in field
                    for (int i = 1; i < userSelection.length; i++) {
                        change += userSelection[i];
                        if (i < userSelection.length - 1)
                            change += " ";
                    }
                    course.setDepartment(change);
                    courseService.updateCourse(course);
                    courseService.findCourseByID(course.getId()).displayCourse();
                    break;
                case "3":   // Change Course Number
                    course.setCourseNo(Integer.parseInt(userSelection[1]));
                    courseService.updateCourse(course);
                    courseService.findCourseByID(course.getId()).displayCourse();
                    break;
                case "4":   // Change Section Number
                    course.setSectionNo(Integer.parseInt(userSelection[1]));
                    courseService.updateCourse(course);
                    courseService.findCourseByID(course.getId()).displayCourse();
                    break;
                case "5":   // Change Instructor
                    // Parsing to allow spacing in field
                    for (int i = 1; i < userSelection.length; i++) {
                        change += userSelection[i];
                        if (i < userSelection.length - 1)
                            change += " ";
                    }
                    course.setInstructor(change);
                    courseService.updateCourse(course);
                    courseService.findCourseByID(course.getId()).displayCourse();
                    break;
                case "6":   // Change Number of Credits
                    course.setCredits(Integer.parseInt(userSelection[1]));
                    courseService.updateCourse(course);
                    courseService.findCourseByID(course.getId()).displayCourse();
                    break;
                case "7":   // Change Total Number of Seats
                    course.setTotalSeats(Integer.parseInt(userSelection[1]));
                    courseService.updateCourse(course);
                    courseService.findCourseByID(course.getId()).displayCourse();
                    break;
                case "8":   // Change Prerequisites // TODO
                    break;
                case "9":   // Change Meeting Times // TODO
                    break;
                case "10":  // Change Description
                    // Parsing to allow spacing in field
                    for (int i = 1; i < userSelection.length; i++) {
                        change += userSelection[i];
                        if (i < userSelection.length - 1)
                            change += " ";
                    }
                    course.setDescription(change);
                    courseService.updateCourse(course);
                    courseService.findCourseByID(course.getId()).displayCourse();
                    break;
                case "11":  // Exit
                default:
                    System.out.println("Taking you back to FacultyMenu...");
                    router.navigate("/faculty");

                    // Remove UpdateMenu from HashSet
                    if (router.getScreens().contains(this)) {
                        router.removeScreen(this);
                    }
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
