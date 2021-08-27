package com.revature.portal.services;

import com.revature.portal.datasource.models.Student;
import com.revature.portal.datasource.models.User;
import com.revature.portal.datasource.repositories.UserRepository;
import com.revature.portal.util.exceptions.AuthenticationException;
import com.revature.portal.util.exceptions.InvalidRequestException;
import com.revature.portal.util.exceptions.ResourceNotFoundException;
import com.revature.portal.util.exceptions.ResourcePersistenceException;
import com.revature.portal.web.dtos.UserDTO;

import com.revature.portal.web.dtos.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;
import java.util.regex.Pattern;

/**
 * The UserService class provides a service abstraction layer between the application layer and database connection
 * layer for queries on the users collection. This service layer provides business logic validation and protects against
 * malicious user input, and facilitates transactions between the application and database layers.
 *
 * Authors: Sean Dunn, Marwan Bataineh
 * Date: 19 August 2021
 * Last Modified: 23 August 2021
 */
public class UserService {

    private final UserRepository userRepo;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * The isUserValid method accepts a User object and validates its fields. Method checks for: object validity (not
     * null), first/last name validity (names are not null/empty, do not exceed 24 characters, do not contain illegal
     * characters), username validity (username is not null/empty, is between 5-20 characters long, does not contain
     * illegal characters, starts and ends with alphanumeric), and password validity (password is not null/empty).
     * TODO: mostly complete, but there is the question of how to validate an encrypted password (see below)
     *
     * @param user - The User object containing user information that needs to be validated.
     * @return - Returns true if user fields passed validity checks; false if one or more user fields did not pass
     *  validity checks.
     */
    public boolean isUserValid(User user) {

        /* Ensure User object exists */
        if (user == null) return false;

        /* Check first/lase names for: null/empty, no greater than 99 characters (protect against malicious mega dump),
            numbers, illegal characters */
        final String VALID_NAME_PATTERN = "^[^±!@£$%^&*_+§¡€#¢§¶•ªº«\\\\/<>?:;|=.,0-9]{1,99}$";
        Pattern pattern = Pattern.compile(VALID_NAME_PATTERN);

        String firstName = user.getFirstName();
        if (firstName == null ||
                firstName.trim().equals("") ||
                !pattern.matcher(firstName).find()) {
            return false;
        }

        String lastName = user.getLastName();
        if (lastName == null ||
                lastName.trim().equals("") ||
                !pattern.matcher(lastName).find()) {
            return false;
        }

        /* Check email for: null/empty, unwanted characters, length, proper domain/domain name fields, username field,
            etc. */
        //TODO: use regex expression to simplify this validation for email
        String email = user.getEmail();

        String eUsername = "";
        String domainFull = "";
        try {
            eUsername = email.split("@")[0];
            domainFull = email.split("@")[1];
        } catch(Exception e) {
            return false;
        }
        if(eUsername.length() < 3) return false;
        if(eUsername.trim().equals("")) return false;
        if(eUsername.matches("\\s+") || eUsername.matches("[^A-Za-z0-9\\.\\-]")) return false;
        if(domainFull.trim().equals("")) return false;

        String domainName = "";
        String domain = "";
        try{
            domainName = domainFull.split("\\.")[0];
            domain = domainFull.split("\\.")[1];
        } catch(Exception e) {
            return false;
        }
        if(domainName.equals("") || domainName.trim().equals("") || domainName.matches("\\s+")) return false;
        if(domain.equals("") || domain.trim().equals("") || domain.matches("\\s+")) return false;

        /* Check username for: null/empty, at least 5 characters long (no greater than 20), starts and ends with an
            alphanumeric character illegal characters */
        final String VALID_USERNAME_PATTERN = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$";
        pattern = Pattern.compile(VALID_USERNAME_PATTERN);

        String username = user.getUsername();
        if (username == null ||
                username.trim().equals("") ||
                !pattern.matcher(username).find()) {
            return false;
        }

        /* Check password for: null/empty */
        // TODO: the password will be encrypted by this point, so how can we validate it?
        return user.getPassword() != null && !user.getPassword().trim().equals("");
    }

    /**
     * Query the database for an existing user under the given username.
     *
     * @param username - Username being checked against database for status (taken or not taken).
     * @return - True if the username is taken; false if otherwise.
     */
    public boolean isUsernameTaken(String username) {
        return userRepo.findUserByUsername(username) != null;
    }

    /**
     * Query the database for an existing user under the given email.
     *
     * @param email - Email being checked against database for status (taken or not taken).
     * @return - True if the email is taken; false if otherwise.
     */
    public boolean isEmailTaken(String email) {
        return userRepo.findUserByEmail(email) != null;
    }

    /**
     * The register method accepts a User object and passes it to the database access layer for the information to be
     * stored after ensuring the input fields are valid, and the entry is not a duplicate on any unique fields.
     *
     * @param newUser - The User object containing user information that should be saved to the database.
     * @return - Returns the returned User object from save method if no exception was thrown.
     */
    public UserDTO register(User newUser) {

        if (!isUserValid(newUser))
            throw new InvalidRequestException("Invalid user data provided!");

        boolean isUsernameTaken = isUsernameTaken(newUser.getUsername());
        boolean isEmailTaken = isEmailTaken(newUser.getEmail());

        /* Check if username and/or email are taken */
        if (isUsernameTaken && isEmailTaken)
            throw new ResourcePersistenceException("Provided Username and Email are taken!");
        else if (isUsernameTaken)
            throw new ResourcePersistenceException("Provided Username is taken!");
        else if (isEmailTaken)
            throw new ResourcePersistenceException("Provided email is already taken!");

        try {
            // If user already exists, register will fail and return null
            newUser = userRepo.save(newUser);
            logger.info("userRepo.save() invoked!");
            return new UserDTO(newUser);
        } catch (InvalidRequestException ire) {
            throw new ResourcePersistenceException("ERROR: User already exists in database!");
        } catch (Exception e) {
            logger.error("An unexpected exception occurred", e);
            return null;
        }
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

        UserDTO authUser = userRepo.findUserByCredentials(username, password);

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

        UserDTO user = userRepo.findById(id);

        if (user == null) {
            throw new ResourceNotFoundException("User with id=" + id + " not found!");
        }

        return user;

    }

    public UserDTO findUserByUsername(String username) {

        if(username == null || username.trim().equals("")) {
            throw new InvalidRequestException("Empty username query!");
        }

        UserDTO user = userRepo.findUserByUsername(username);

        if(user == null) {
            throw new ResourceNotFoundException("User with that username does not exist!");
        }

        return user;

    }

    public UserDTO findUserByEmail(String email) {

        if(email == null || email.trim().equals("")) {
            throw new InvalidRequestException("Empty email query!");
        }

        UserDTO user =  userRepo.findUserByEmail(email);

        if(user == null) {
            throw new ResourceNotFoundException("User with that email does not exist!");
        }

        return user;

    }

    /**
     * The retrieveUsers method receives a list of all users from the database access layer and returns it.
     * @return - Returns a list of all users in the database.
     */
    public List<UserDTO> retrieveUsers() {
        return userRepo.retrieveUsers();
    }


    /**
     * The updateUser method accepts a User object from the application layer and passes it to the database access layer
     * to update an existing user. If the user does not exist, or the operation otherwise fails, a
     * ResourcePersistenceException will be thrown (assuming an exception from the database access layer is not thrown
     * first).
     */
    public UserDTO updateUser(User user) {

        Student modelUser = new Student("Validname", "Validlast", "valid.email@valid.com", "validusername", "validpassword");

        if(user.getFirstName() != null) modelUser.setFirstName(user.getFirstName());
        if(user.getLastName() != null) modelUser.setLastName(user.getLastName());
        if(user.getEmail() != null) modelUser.setEmail(user.getEmail());
        if(user.getUsername() != null) modelUser.setUsername(user.getUsername());
        if(user.getPassword() != null) modelUser.setPassword(user.getPassword());

        if(!isUserValid(modelUser)) {
            throw new InvalidRequestException("Provided user data is not valid!");
        }

        return userRepo.update(user);
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
