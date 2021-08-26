package com.revature.portal.web.servlet_helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.portal.datasource.repositories.UserRepository;
import com.revature.portal.services.UserService;

public class AvailabilityChecker {

    final UserService userService;

    public AvailabilityChecker() {
        this.userService = new UserService(new UserRepository(new ObjectMapper()));
    }

    /**
     * The checkAvailability method is a helper method which provides an HTTPResponse-ready message to include the
     * availability of the given username and password.
     *
     * @param username
     * @param email
     * @return HTTPResponse-ready message if there exist availability conflicts with either the username or password
     *  given; or null if no conflicts exist.
     */
    public String checkAvailability(String username, String email) {
        final String usernameTakenResult = "\"username\": \"taken\"";
        final String emailTakenResult = "\"email\": \"taken\"";
        boolean isUsernameTaken = userService.isUsernameTaken(username);
        boolean isEmailTaken = userService.isEmailTaken(email);
        boolean isResultEmpty = true;

        String result= "";
        if(isUsernameTaken) {
            result += usernameTakenResult;
            isResultEmpty = false;
        }
        if(isEmailTaken) {
            if(!isResultEmpty)  result += ", ";
            result += emailTakenResult;
        }

        // "availability": "{ "username": "taken", "email": "taken" }"

        if(!isResultEmpty) return "\"availability\": { " + result + " }";
        return null; // If there are no availability conflicts
    }

}
