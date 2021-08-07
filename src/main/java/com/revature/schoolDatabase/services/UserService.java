package com.revature.schoolDatabase.services;

import com.revature.schoolDatabase.models.Course;
import com.revature.schoolDatabase.models.Faculty;
import com.revature.schoolDatabase.models.Person;
import com.revature.schoolDatabase.models.Student;
//import com.revature.schoolDatabase.util.exceptions.InvalidRequestException;
import com.revature.schoolDatabase.repositories.UserRepository;
import com.revature.schoolDatabase.util.exceptions.InvalidRequestException;

public class UserService {

    // Variables
    private final UserRepository userRepo;
    private Student stud;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
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
        return userRepo.findUserByCredentials(username, password);
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
