package com.revature.schoolDatabase.screens.menus;

import com.revature.schoolDatabase.models.*;
import com.revature.schoolDatabase.services.CourseService;
import com.revature.schoolDatabase.services.UserService;
import com.revature.schoolDatabase.util.ScreenRouter;
import com.revature.schoolDatabase.util.exceptions.ResourcePersistenceException;

import java.io.BufferedReader;
import java.util.List;

public class UpdateMenu<T> extends Menu{
    // Variables
    private T object;
    private final UserService userService;
    private final CourseService courseService;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

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
            Course oldCourse = course;  // Used for updating user schedules
            String flag = "";
            System.out.println();
            course.displayCourse();
            displayMenu();

            // Split user input on whitespace
            String[] userSelection = consoleReader.readLine().split(" ");
            System.out.println();
            String change = "";

            try {
                switch (userSelection[0]) {
                    case "1":   // Change Title
                        // Parsing to allow spacing in field
                        for (int i = 1; i < userSelection.length; i++) {
                            change += userSelection[i];
                            if (i < userSelection.length - 1)
                                change += " ";
                        }
                        course.setTitle(change);
                        break;
                    case "2":   // Change Dept // TODO Auto change deptShort as well
                        // Parsing to allow spacing in field
                        for (int i = 1; i < userSelection.length; i++) {
                            change += userSelection[i];
                            if (i < userSelection.length - 1)
                                change += " ";
                        }
                        course.setDepartment(change);
                        flag = "dept";
                        break;
                    case "3":   // Change Course Number
                        course.setCourseNo(Integer.parseInt(userSelection[1]));
                        flag = "courseNo";
                        break;
                    case "4":   // Change Section Number
                        course.setSectionNo(Integer.parseInt(userSelection[1]));
                        flag = "sectionNo";
                        break;
                    case "5":   // Change Instructor
                        // Parsing to allow spacing in field
                        for (int i = 1; i < userSelection.length; i++) {
                            change += userSelection[i];
                            if (i < userSelection.length - 1)
                                change += " ";
                        }
                        course.setInstructor(change);
                        break;
                    case "6":   // Change Number of Credits
                        course.setCredits(Integer.parseInt(userSelection[1]));
                        break;
                    case "7":   // Change Total Number of Seats
                        course.setTotalSeats(Integer.parseInt(userSelection[1]));
                        break;
                    case "8":   // Change Prerequisites
                        if (course.getPrerequisites() != null) {
                            System.out.println("Change Course Prerequisites");
                            for (int i = 0; i < course.getPrerequisites().size(); i++) {
                                System.out.println(i + ".) ");
                                System.out.println(course.getPrerequisites().get(i));
                            }
                            System.out.println("Syntax: <choice> <newDept> <courseNo> <newCredits>");
                            String[] updatePreReqs = consoleReader.readLine().split(" ");
                            try {
                                course.getPrerequisites().remove(Integer.parseInt(updatePreReqs[0]));
                                PreReq newPreReq = new PreReq(updatePreReqs[1], Integer.parseInt(updatePreReqs[2]), Integer.parseInt(updatePreReqs[3]));
                                course.getPrerequisites().add(newPreReq);
                            } catch (Exception e) {
                                System.out.println(ANSI_RED + "ERROR: Prerequisites not updated." + ANSI_RESET);
                            }
                        }

                        break;
                    case "9":   // Change Meeting Times
                        if (course.getMeetingTimes() != null) {
                            System.out.println("Change Course Meeting Times");
                            for (int i = 0; i < course.getMeetingTimes().size(); i++) {
                                System.out.println(i + ".) ");
                                System.out.println(course.getMeetingTimes().get(i));
                            }
                            System.out.println("Syntax: <choice> <newDay> <newStartTime> <newEndTime> <newCourseType>");
                            String[] updateMeets = consoleReader.readLine().split(" ");
                            try {
                                course.getMeetingTimes().remove(Integer.parseInt(updateMeets[0]));
                                MeetingTime newMeet = new MeetingTime(updateMeets[1], Integer.parseInt(updateMeets[2]), Integer.parseInt(updateMeets[3]), updateMeets[4]);
                                course.getMeetingTimes().add(newMeet);
                                flag = "meeting " + updateMeets[0];
                            } catch (Exception e) {
                                System.out.println(ANSI_RED + "ERROR: Meeting Times not updated." + ANSI_RESET);
                            }
                        }

                        break;
                    case "10":  // Change Description
                        // Parsing to allow spacing in field
                        for (int i = 1; i < userSelection.length; i++) {
                            change += userSelection[i];
                            if (i < userSelection.length - 1)
                                change += " ";
                        }
                        course.setDescription(change);
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

                try {
                    courseService.updateCourse(course);
                    courseService.findCourseByID(course.getId()).displayCourse();

                    // Update users who were affected by changes
                    // First determine if core info changed
                    if (!flag.isEmpty()) {
                        String[] splitFlag = flag.split(" ");
                        try {
                            List<Person> users = userService.retrieveUsers();
                            for (Person user : users) {
                                if (user.getSchedule() != null) {
                                    for (Schedule sched : user.getSchedule()) {
                                        switch (splitFlag[0]) {
                                            case "dept":
                                                if (sched.getCourseDept().equals(oldCourse.getDeptShort()))
                                                    sched.setCourseDept(course.getDeptShort());
                                                break;
                                            case "courseNo":
                                                if (sched.getCourseNo() == oldCourse.getCourseNo())
                                                    sched.setCourseNo(course.getCourseNo());
                                                break;
                                            case "sectionNo":
                                                if (sched.getSectionNo() == oldCourse.getSectionNo())
                                                    sched.setSectionNo(course.getSectionNo());
                                            case "meeting":
                                                int index = Integer.parseInt(splitFlag[1]);
                                                if (sched.getMeetingTimes().get(index).equals(oldCourse.getMeetingTimes().get(index))) {
                                                    sched.getMeetingTimes().remove(index);
                                                    sched.getMeetingTimes().add(course.getMeetingTimes().get(course.getMeetingTimes().size() - 1));
                                                }
                                        }
                                    }
                                }
                                // Persist changes to database
                                userService.updateUser(user);
                            }
                        } catch (Exception e) {
                            System.out.println(ANSI_RED + "ERROR: Users not updated." + ANSI_RESET);
                        }
                    }

                } catch (Exception e) {
                    System.out.println(ANSI_RED + "ERROR: Field not updated." + ANSI_RESET);
                }
            } catch (ArrayIndexOutOfBoundsException aie) {
                System.out.println(ANSI_RED + "ERROR: Incorrect criteria, try again." + ANSI_RESET);
            }
        }
        else {
            System.out.println("Taking you back to FacultyMenu...");
            router.navigate("/faculty");
        }



    }

}
