package com.revature.schoolDatabase.services;

import com.revature.schoolDatabase.models.Course;
import com.revature.schoolDatabase.models.Faculty;
import com.revature.schoolDatabase.models.Person;
import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.exceptions.InvalidRequestException;

public class UserService {

    // Variables
//    private final UserRepository userRepo;
    private Student stud;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";

//    public UserService(UserRepository userRepo) {
//        this.userRepo = userRepo;
//    }


    /**
     * Lists all courses in catalog
     */
    public void showCourses() {
        System.out.println(ANSI_CYAN + "Title: Introduction to Programming\n" + ANSI_RESET +
                            "Department: COSC\n" +
                            "Course ID: 2784\n");

        // TODO List all courses currently stored in database
    }

    /**
     * Lists courses in catalog according to flags
     *
     * @param flags -- used to specify which courses to display
     *             List of flags:
     *                  -- 'open' = courses with open seats
     *                  -- 'closed' = courses which have no open seats
     *                  -- 'user' = courses in a given Person's schedule
     *                  -- 'schedule' = courses that fit in schedule
     *                  -- 'dept' = courses in a given Department
     */
    public void showCourses(Person user, String... flags) {
        System.out.println(ANSI_CYAN + "Title: Introduction to Programming\n" + ANSI_RESET +
                "Department: COSC\n" +
                "Course ID: 2784\n");
    }

    /**
     * Adds a new course to a Student's schedule
     *
     * @param stud
     * @param courseID
     */
    public void addCourse(Student stud, int courseID) {
        // TODO -------------------------------
        // Verify student is in database

        // Find course in database given courseID

        // Compare new course info with student (schedule, etc) to ensure the add is valid

        // Add new course to student's schedule

    }

    /**
     * Creates a new course, given user is a Faculty member
     *
     * @param fac
     * @param newCourse
     */
    public void createCourse(Faculty fac, Course newCourse) {
        // TODO -------------------------------
        // Verify faculty is in database and is qualified to create a course

        // Verify that the new course does not already exist in the database

        // Store course in database with given faculty as "professor"
    }


    /**
     * Takes in a non-null Person, validates its fields, and attempts to persist it to the datasource.
     *
     * @param
     * @return
     */
    public Person register(Person newUser) {

        if (!isUserValid(newUser)) {
            throw new InvalidRequestException("Invalid user data provided!");
        }

//        if (userRepo.findUserByUsername(newUser.getUsername()) != null) {
//            throw new ResourcePersistenceException("Provided username is already taken!");
//        }

        return newUser;
    }

    /**
     * Function is called from LoginScreen
     * Checks if username/password combo is stored in the database.
     * If it is, return user, else return null.
     *
     * @param username
     * @param password
     */
    public Person login(String username, String password) {

        if (username == null || username.trim().equals("") || password == null || password.trim().equals("")) {
            throw new InvalidRequestException("Invalid user credentials provided!");
        }
        // TODO Change login as database is connected
        else {
            if (username.equals("student"))
                return new Student("test", "test", username, password);
            else if (username.equals("faculty"))
                return new Faculty("test", "test", username, password);
            else return null;
        }
    }

    /**
     * Validates given user credentials to ensure fields are not empty, null, invalid, etc.
     *
     * @param user
     * @return true if user is valid, false otherwise
     */
    public boolean isUserValid(Person user) {
        if (user == null) return false;
        if (user.getFirstName() == null || user.getFirstName().trim().equals("")) return false;
        if (user.getLastName() == null || user.getLastName().trim().equals("")) return false;
//        if (user.getEmail() == null || user.getEmail().trim().equals("")) return false;
        if (user.getUsername() == null || user.getUsername().trim().equals("")) return false;
        return user.getPassword() != null && !user.getPassword().trim().equals("");
    }

}
