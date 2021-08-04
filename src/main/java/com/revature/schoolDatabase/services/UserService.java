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

//    public UserService(UserRepository userRepo) {
//        this.userRepo = userRepo;
//    }


    /**
     * Lists all courses in catalog
     */
    public void showCourses() {
        System.out.println("Course ID: 2784\n" +
                            "Title: Introduction to Programming\n" +
                            "Department: COSC\n");
    }

    /**
     * Lists courses in catalog according to flag
     *
     * @param flag -- used to specify which courses to display
     *             List of flags:
     *                  -- 'o' = courses with open seats
     *                  -- 'c' = courses which have no open seats
     *                  -- 'u' = courses in a given Student's schedule
     *                  -- 's' = courses that fit in schedule
     *                  -- 'd' = courses in a given Department
     */
    public void showCourses(char flag) {
        System.out.println("Course ID: 2784\n" +
                "Title: Introduction to Programming\n" +
                "Department: COSC\n");
    }

    /**
     * Adds a new course to a Student's schedule
     *
     * @param stud
     * @param courseID
     */
    public void addCourse(Student stud, int courseID) {

    }

    /**
     * Creates a new course, given user is a Faculty member
     *
     * @param fac
     * @param newCourse
     */
    public void createCourse(Faculty fac, Course newCourse) {

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
     *
     * @param username
     * @param password
     */
    public Person login(String username, String password) {

        if (username == null || username.trim().equals("") || password == null || password.trim().equals("")) {
            throw new InvalidRequestException("Invalid user credentials provided!");
        }
        else {
            return new Student("test", "test", username, password);
        }
    }

    public boolean isUserValid(Person user) {
        if (user == null) return false;
        if (user.getFirstName() == null || user.getFirstName().trim().equals("")) return false;
        if (user.getLastName() == null || user.getLastName().trim().equals("")) return false;
//        if (user.getEmail() == null || user.getEmail().trim().equals("")) return false;
        if (user.getUsername() == null || user.getUsername().trim().equals("")) return false;
        return user.getPassword() != null && !user.getPassword().trim().equals("");
    }

}
