package com.revature.schoolDatabase.screens.menus;

import com.revature.schoolDatabase.models.*;
import com.revature.schoolDatabase.screens.RegisterScreen;
import com.revature.schoolDatabase.services.CourseService;
import com.revature.schoolDatabase.services.UserService;
import com.revature.schoolDatabase.util.ScreenRouter;
import com.revature.schoolDatabase.util.exceptions.ResourcePersistenceException;
import com.revature.schoolDatabase.util.exceptions.SchedulingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.util.List;

import static com.revature.schoolDatabase.util.ProgramManager.*;

public class UpdateMenu<T> extends Menu{
    // Variables
    private T object;
    private final UserService userService;
    private final CourseService courseService;
    private final Logger logger = LogManager.getLogger(UpdateMenu.class);

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
            Course oldCourse = new Course(course.getTitle(), course.getDepartment(), course.getCourseNo(), course.getSectionNo());  // Used for updating user schedules
            String flag = "";
            System.out.println();
            course.displayCourse();
            displayMenu();

            // Split user input on whitespace
            String[] userSelection = consoleReader.readLine().split(" ");
            System.out.println();
            String change = "";

            // Parsing to allow spacing in field
            if (userSelection.length <= 1 && !userSelection[0].equals("11")) {
                logger.debug("ERROR: Not enough criteria!");
                System.out.println(ANSI_RED + "ERROR: Not enough criteria!" + ANSI_RESET);
                System.out.println("Usage: <choice> <newValue>");
            }
            else {
                try {
                    switch (userSelection[0]) {
                        case "1":   // Change Title
                            for (int i = 1; i < userSelection.length; i++) {
                                change += userSelection[i];
                                if (i < userSelection.length - 1)
                                    change += " ";
                            }
                            course.setTitle(change);
                            break;
                        case "2":   // Change Dept
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
                            if (course.getOpenSeats() > course.getTotalSeats())
                                course.setOpenSeats(course.getOpenSeats());
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
                                    logger.error(e.getMessage());
                                    logger.debug("ERROR: Prerequisites not updated.");
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
                                    logger.error(e.getMessage());
                                    logger.debug("ERROR: Meeting Times not updated.");
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
                        course = courseService.findCourseByID(course.getId());
//                        course.displayCourse();

                        // Update users who were affected by changes
                        // First determine if core info changed
                        if (!flag.isEmpty()) {
                            Schedule newSchedule = new Schedule(course.getDeptShort(), course.getCourseNo(), course.getSectionNo(), course.getMeetingTimes());
                            try {
                                List<Person> users = userService.retrieveUsers();
                                for (Person user : users) {
                                    if (user.getSchedule() != null) {
                                        List<Schedule> schedule = user.getSchedule();
                                        for (Schedule sched : schedule) {
                                            if ((sched.getCourseDept().equals(oldCourse.getDeptShort())) && (sched.getCourseNo() == oldCourse.getCourseNo()) && (sched.getSectionNo() == oldCourse.getSectionNo())) {
                                                user.removeFromSchedule(sched);
                                                user.addToSchedule(newSchedule);
                                            }
                                        }
                                    }
                                    // Persist changes to database
                                    userService.updateUser(user);
                                }
                            } catch (Exception e) {
                                logger.error(e.getMessage());
                                logger.debug("ERROR: Users not updated.");
                                System.out.println(ANSI_RED + "ERROR: Users not updated." + ANSI_RESET);
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        logger.debug("ERROR: Field not updated.");
                        System.out.println(ANSI_RED + "ERROR: Field not updated." + ANSI_RESET);
                    }
                } catch (ArrayIndexOutOfBoundsException aie) {
                    logger.error(aie.getMessage());
                    logger.debug("ERROR: Incorrect criteria, try again.");
                    System.out.println(ANSI_RED + "ERROR: Incorrect criteria, try again." + ANSI_RESET);
                }
            }
        }
        else {
            System.out.println("Taking you back to FacultyMenu...");
            // Remove UpdateMenu from HashSet
            if (router.getScreens().contains(this))
                router.removeScreen(this);

            router.navigate("/faculty");
        }



    }

}
