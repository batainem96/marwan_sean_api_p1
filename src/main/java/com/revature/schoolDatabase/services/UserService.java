package com.revature.schoolDatabase.services;

import com.revature.schoolDatabase.models.Course;
import com.revature.schoolDatabase.models.Faculty;
import com.revature.schoolDatabase.models.Student;

public class UserService {

    // Variables
//    private final UserRepository userRepo;

//    public UserService(UserRepository userRepo) {
//        this.userRepo = userRepo;
//    }


    public void showCourses() {
        System.out.println("Course ID: 2784\n" +
                            "Title: Introduction to Programming\n" +
                            "Department: COSC\n");
    }

    /**
     *
     * @param flag -- used to specify which courses to display
     */
    public void showCourses(char flag) {
        System.out.println("Course ID: 2784\n" +
                "Title: Introduction to Programming\n" +
                "Department: COSC\n");
    }

    /**
     * Used to add a new course to a Student's schedule
     *
     * @param stud
     * @param newCourse
     */
    public void addCourse(Student stud, Course newCourse) {

    }

    /**
     * Used to create a new course
     *
     * @param fac
     * @param newCourse
     */
    public void createCourse(Faculty fac, Course newCourse) {

    }


    /**
     * Takes in a non-null AppUser, validates it fields, and attempt to persist it to the datasource.
     *
     * @param newUser
     * @return
     */
//    public AppUser register(AppUser newUser) {
//
//        if (!isUserValid(newUser)) {
//            throw new InvalidRequestException("Invalid user data provided!");
//        }
//
//        if (userRepo.findUserByUsername(newUser.getUsername()) != null) {
//            throw new ResourcePersistenceException("Provided username is already taken!");
//        }
//
//        return userRepo.save(newUser);
//
//    }

//    public AppUser login(String username, String password) {
//
//        if (username == null || username.trim().equals("") || password == null || password.trim().equals("")) {
//            throw new InvalidRequestException("Invalid user credentials provided!");
//        }
//
//        return userRepo.findUserByCredentials(username, password);
//
//    }
//
//    public boolean isUserValid(AppUser user) {
//        if (user == null) return false;
//        if (user.getFirstName() == null || user.getFirstName().trim().equals("")) return false;
//        if (user.getLastName() == null || user.getLastName().trim().equals("")) return false;
//        if (user.getEmail() == null || user.getEmail().trim().equals("")) return false;
//        if (user.getUsername() == null || user.getUsername().trim().equals("")) return false;
//        return user.getPassword() != null && !user.getPassword().trim().equals("");
//    }

}
