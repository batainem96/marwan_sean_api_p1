package com.revature.schoolDatabase.services;

import com.revature.schoolDatabase.models.Person;
import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.repositories.UserRepository;
import com.revature.schoolDatabase.util.exceptions.InvalidRequestException;
import com.revature.schoolDatabase.util.exceptions.ResourcePersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UserService {

    // Variables
    private final UserRepository userRepo;
    private Student stud;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    private final Logger logger = LogManager.getLogger(UserService.class);

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
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
        if (user.getUsername() == null || user.getUsername().trim().equals("")) return false;
        return user.getPassword() != null && !user.getPassword().trim().equals("");
    }

    /**
     * Takes in a non-null Person, validates its fields, and attempts to persist it to the datasource.
     *
     * @param
     * @return
     */
    public Person register(Person newUser) {

        if (!isUserValid(newUser)) {
            logger.error("Invalid user data provided!");
            throw new InvalidRequestException("Invalid user data provided!");
        }

        if (userRepo.findUserByCredentials(newUser.getUsername()) != null) {
            logger.error("Provided username is already taken!");
            throw new ResourcePersistenceException("Provided username is already taken!");
        }

        newUser = userRepo.save(newUser);

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
     * Lists all users in database
     */
    public void showUsers() {
        List<Person> users = userRepo.retrieveUsers();
        for (Person person : users) {
            person.displayUser();
        }
    }

    /**
     * Returns all users in database
     */
    public List<Person> retrieveUsers() {
        List<Person> users = userRepo.retrieveUsers();
        return users;
    }


    /**
     * Persist updated user information to the database
     */
    public void updateUser(Person user) {
        boolean result = userRepo.update(user);
        if (!result) {
            logger.error("Failed to update user");
            throw new ResourcePersistenceException("Failed to update user");
        }
    }

    /**
     * Remove given user from the database
     */
    public void deleteUser(Person user) {
        boolean result = userRepo.deleteById(user.getId());
        if (!result) {
            logger.error("Failed to delete user");
            throw new ResourcePersistenceException("Failed to delete user");
        }
    }

}
