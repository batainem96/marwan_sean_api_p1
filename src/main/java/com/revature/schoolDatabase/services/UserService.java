package com.revature.schoolDatabase.services;

import com.revature.schoolDatabase.datasource.models.User;
import com.revature.schoolDatabase.datasource.repositories.UserRepository;
import com.revature.schoolDatabase.util.exceptions.AuthenticationException;
import com.revature.schoolDatabase.util.exceptions.InvalidRequestException;
import com.revature.schoolDatabase.util.exceptions.ResourcePersistenceException;
import com.revature.schoolDatabase.web.dtos.UserDTO;

import com.revature.schoolDatabase.web.dtos.Principal;


import java.util.List;

/**
 * The UserService class provides a service abstraction layer between the application layer and database connection
 * layer. This service layer provides business logic validation and protects against malicious user input, and
 * facilitates transactions between the application and database layers.
 *
 * Authors: Sean Dunn, Marwan Bataineh
 * Date: 19 August 2021
 * Last Modified: 19 August 2021
 */
public class UserService {

    private final UserRepository userRepo;

    /* TODO: Sean - do you want to keep these ANSI values for something else? */
//    public static final String ANSI_RESET = "\u001B[0m";
//    public static final String ANSI_PURPLE = "\u001B[35m";
//    public static final String ANSI_CYAN = "\u001B[36m";

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * The isUserValid method accepts a User object and validates its fields. Method checks for: object validity (not
     * null), first/last name validity (names are not null/empty), username validity (username is not null/empty), and
     * password validity (password is not null/empty).
     * TODO: Expand validity checking to include: first/last names do not contain numbers or special characters (i.e.
     *  $, @, &, *, etc.), username/password does not contain illegal characters (protect against db code injection -
     *  i.e. some query string [db.users.find()]!), email (currently no email validity checking/protection).
     *
     * @param user - The User object containing user information that needs to be validated.
     * @return - Returns true if user fields passed validity checks; false if one or more user fields did not pass
     *  validity checks.
     */
    public boolean isUserValid(User user) {
        if (user == null) return false;
        if (user.getFirstName() == null || user.getFirstName().trim().equals("")) return false;
        if (user.getLastName() == null || user.getLastName().trim().equals("")) return false;
        if (user.getUsername() == null || user.getUsername().trim().equals("")) return false;
        return user.getPassword() != null && !user.getPassword().trim().equals("");
    }

    /**
     * The register method accepts a User object and passes it to the database access layer for the information to be
     * stored after ensuring the input fields are valid, and the entry is not a duplicate on any unique fields.
     *
     * @param newUser - The User object containing user information that should be saved to the database.
     * @return - Returns the returned User object from save method if no exception was thrown.
     */
    public User register(User newUser) {

        if (!isUserValid(newUser)) {
            throw new InvalidRequestException("Invalid user data provided!");
        }

        if (userRepo.findUserByCredentials(newUser.getUsername()) != null) {
            throw new ResourcePersistenceException("Provided username is already taken!");
        }

        // Return user object from save method call (if it's null, something went wrong)
        return userRepo.save(newUser);
    }

    /**
     * The login method passes an authentication request from the application layer to the database access layer, where
     * it is checked for confirmation. The authentication request is provided as two parameters representing the user's
     * username and password. If the provided user credentials are confirmed, a Principal object is returned containing
     * pertinent user information to establish a session. If the credentials are rejected, an AuthenticationException is
     * thrown.
     * TODO: Separate validation logic from this method and relocate it to a different method.
     *
     * @param username - The inputted username by the user.
     * @param password - The inputted password by the user.
     * @return - Returns a Principal object containing pertinent user information returned from the database read
     *  operation.
     */
    public Principal login(String username, String password) {
        if (username == null || username.trim().equals("") || password == null || password.trim().equals("")) {
            throw new InvalidRequestException("Invalid user credentials provided!");
        }

        User authUser = userRepo.findUserByCredentials(username, password);

        if (authUser == null) {
            throw new AuthenticationException("Invalid credentials provided!");
        }

        return new Principal(authUser);
    }

    /**
     * Finds and retrieves user from database with given id.
     * The user is converted into a DTO.
     *
     * @param id
     * @return
     */
    public UserDTO findUserById(String id) {

        if (id == null || id.trim().isEmpty()) {
            throw new InvalidRequestException("Invalid id provided");
        }

        User user = userRepo.findById(id);

        if (user == null) {
            throw new ResourceNotFoundException();
        }

        return new UserDTO(user);

    }

    /**
     * The retrieveUsers method receives a list of all users from the database access layer and returns it.
     * @return - Returns a list of all users in the database.
     */
    public List<UserDTO> retrieveUsers() {
        return userRepo.retrieveUsers()
                        .stream()
                        .map(UserDTO::new)
                        .collect(Collectors.toList());
    }


    /**
     * The updateUser method accepts a User object from the application layer and passes it to the database access layer
     * to update an existing user. If the user does not exist, or the operation otherwise fails, a
     * ResourcePersistenceException will be thrown (assuming an exception from the database access layer is not thrown
     * first).
     */
    public void updateUser(User user) {
        if (!userRepo.update(user)) {
            throw new ResourcePersistenceException("Failed to update user");
        }
    }

    /**
     * The deleteUser method accepts a User object from the application layer and passes it to the database access layer
     * to delete an existing user. If the user does not exist, or the operation otherwise fails, a
     * ResourcePersistenceException will be thrown (assuming an exception from the database access layer is not thrown
     * first).
     */
    public void deleteUser(User user) {
        boolean result = userRepo.deleteById(user.getId());
        if (!result) {
            throw new ResourcePersistenceException("Failed to delete user");
        }
    }

}
