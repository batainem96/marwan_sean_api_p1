package com.revature.portal.web.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import com.revature.portal.datasource.models.Student;
import com.revature.portal.datasource.models.User;
import com.revature.portal.services.UserService;
import com.revature.portal.util.exceptions.AuthenticationException;
import com.revature.portal.util.exceptions.InvalidRequestException;
import com.revature.portal.util.exceptions.ResourceNotFoundException;
import com.revature.portal.util.exceptions.ResourcePersistenceException;
import com.revature.portal.web.dtos.ErrorResponse;
import com.revature.portal.web.dtos.Principal;
import com.revature.portal.web.dtos.UserDTO;

// ------------------------------ Servlet Helpers
import com.revature.portal.web.servlet_helpers.AvailabilityChecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * The RegisterServlet HttpServlet class processes register requests from the web application.
 *
 * Date: 19 August 2021
 * Last Modified: 23 August 2021
 */
public class UserServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private final UserService userService;
    private final ObjectMapper mapper;

    public UserServlet(UserService userService, ObjectMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    /**
     *  Authorization function: checks principal for id/username/token/role/etc to decide if current user is
     *  allowed to perform current action.
     */
    public boolean authorize(HttpServletRequest req, HttpServletResponse resp) throws JsonProcessingException {
        // Get the principal information from the request, if it exists.
        Principal requestingUser = (Principal) req.getAttribute("principal");
        boolean isFaculty = requestingUser.getRole().equals("faculty");
        boolean isStudent = requestingUser.getRole().equals("student");
        boolean isUserAccessingOwnData = req.getParameter("id") != null ||
                requestingUser.getId().equals(req.getParameter("id"));

        // Check to see if there was a valid principal attribute
        if (requestingUser == null) {
            String msg = "No session found, please login.";
            ErrorResponse errResp = new ErrorResponse(401, msg);
            throw new AuthenticationException(mapper.writeValueAsString(errResp));
        } else if (requestingUser.getRole() == null) {
            String msg = "Unauthorized attempt to access endpoint made by: " + requestingUser.getUsername();
            ErrorResponse errResp = new ErrorResponse(403, msg);
            throw new AuthenticationException(mapper.writeValueAsString(errResp));
        } else if (isUserAccessingOwnData) { // Student allowed access to own information
            return true;
        } else if (!requestingUser.getRole().equals("faculty")) {
            String msg = "Unauthorized attempt to access endpoint made by: " + requestingUser.getUsername();
            ErrorResponse errResp = new ErrorResponse(403, msg);
            throw new AuthenticationException(mapper.writeValueAsString(errResp));
        }
        return true;
    }

    public boolean authorizeCurrentUser(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        // Get the principal information from the request, if it exists.
        Principal requestingUser = (Principal) req.getAttribute("principal");

        // Check to see if there was a valid principal attribute
        if (requestingUser == null) {
            String msg = "No session found, please login.";
            ErrorResponse errResp = new ErrorResponse(401, msg);
            throw new AuthenticationException(mapper.writeValueAsString(errResp));
        } else {
            // Assert that the requesting user's id matches the id of the user to be updated
            if (!requestingUser.getId().equals(user.getId())) {
                String msg = "Unauthorized attempt to access endpoint made by: " + requestingUser.getUsername();
                ErrorResponse errResp = new ErrorResponse(403, msg);
                throw new AuthenticationException(mapper.writeValueAsString(errResp));
            }
        }
        return true;
    }

    /**
     *  Overriding the generic service function allows us to define our own doPatch method, as it is not native to
     *  the HttpServlet class.
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else super.service(req, resp);
    }

    /**
     * Handles GET requests to "/users/*" and will determine if the request is a simple availability check
     * (see {@link #availabilityCheck(HttpServletRequest, HttpServletResponse)}) or a resource retrieval.
     * If the request is for resource retrieval, security checks will ensure that the requesting user is both
     * authenticated and authorized to perform the action.
     *
     * @param req The incoming request from the client.
     * @param resp The outgoing response intended for the client.
     * @throws IOException Occurs if there are issues with writing to the response body
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        /*
            * Get the Request Fragments
            * Get the Request Parameters
         */
        String[] reqFrags = req.getRequestURI().split("/");
        String idParam = req.getParameter("id");
        String usernameParam = req.getParameter("username");
        String emailParam = req.getParameter("email");

        //------------------------------------------------------------------------------------------

        /*
            This segment checks to see if the requester is trying to check the availability of a username
            or email address. We do this by checking to see if "/availability" was included on the end
            of the request URI.
         */


//        boolean checkingAvailability = reqFrags[reqFrags.length - 1].equals("availability");
//
//        if (checkingAvailability) {
//            availabilityCheck(req, resp);
//            return; // end here, do not proceed to the remainder of the method's logic
//        }

        //------------------------------------------------------------------------------------------

        /*
            This segment checks to see if the requesting user is authenticated and has the proper
            permissions to perform the action. This logic is what secures our endpoint and prevents
            unauthorized access and usage.
         */

        try {
            authorize(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            respWriter.write(e.getMessage());
            return;
        }

        //------------------------------------------------------------------------------------------

        /*
            This segment is what performs the searching logic for retrieving users based on the request.
            If the user included a request parameter called "id", we will simply search the data source
            for a single user with the provided id. Otherwise, we will assume that they intend to retrieve
            all the users from the data source.
         */

        try {

            if(usernameParam != null) {
                UserDTO user = userService.findUserByUsername(usernameParam);
                respWriter.write(mapper.writeValueAsString(user));
                return;
            }

            if(emailParam != null) {
                UserDTO user = userService.findUserByEmail(emailParam);
                respWriter.write(mapper.writeValueAsString(user));
                return;
            }

            if (idParam == null) {
                List<UserDTO> users = userService.retrieveUsers();
                respWriter.write(mapper.writeValueAsString(users));
                return;
            } else {
                UserDTO user = userService.findUserById(idParam);
                respWriter.write(mapper.writeValueAsString(user));
                return;
            }

        } catch (ResourceNotFoundException rnfe) {
            logger.info(rnfe.getMessage());
            writeErrorResponse(rnfe.getMessage(), 400, resp);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            writeErrorResponse("An unexpected error occurred on the server.", 500, resp);
        }

        //------------------------------------------------------------------------------------------

    }

    /**
     * The doPost method takes in a POST request containing User information, which is constructed into a User
     *      class and persisted to the Mongo Database. If an exception is thrown, for reasons such as invalid
     *      credentials, or credentials already existing in the database, registration logic fails and an error
     *      message is returned to the sender to handle.
     *
     * @param req - HttpServletRequest object
     * @param resp - HttpServletResponse object
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        try {

            ServletInputStream sis = req.getInputStream();
            User newUser = mapper.readValue(sis, User.class);
            Principal principal = new Principal(userService.register(newUser));
            String payload = mapper.writeValueAsString(principal);
            respWriter.write(payload);
            resp.setStatus(201);
            logger.info("User successfully registered!");

        } catch (InvalidRequestException | MismatchedInputException e) {

            // Invalid user info
            e.printStackTrace();
            resp.setStatus(400);
            ErrorResponse errResp = new ErrorResponse(400, e.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.error("Invalid user info!", e);

        } catch (ResourcePersistenceException rpe) {

            // Duplicate user info
            resp.setStatus(409);
            ErrorResponse errResp = new ErrorResponse(409, rpe.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.error("Error writing to database. This was most likely due to duplicate user information.", rpe);

        } catch (IOException ie) {

            resp.setStatus(501);
            ErrorResponse errResp = new ErrorResponse(501, ie.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.error("Error reading input stream", ie);

        } catch (Exception e) {

            resp.setStatus(500);
            ErrorResponse errResp = new ErrorResponse(500, "An unknown exception occurred.");
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.error("An unknown exception occurred.", e);

        }
    }

    /**
     *  The doPut method takes in an HttpServletRequest which is used to replace existing information in the
     *  database. A user with the given id is erased from the database, and a new user with given information
     *  is persisted in its place.
     *  The requesting user must be validated using the authorize function.
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        try {
            ServletInputStream sis = req.getInputStream();
            User user = mapper.readValue(sis, User.class);

            // Ensure user is being updated by the requesting user
            try {
                authorizeCurrentUser(req, resp, user);
            } catch (Exception e) {
                e.printStackTrace();
                respWriter.write(e.getMessage());
                return;
            }

            Principal principal = (Principal) req.getAttribute("principal");
            user.setId(principal.getId());
            UserDTO updatedUser = userService.updateUser(user);
            respWriter.write(mapper.writeValueAsString(updatedUser));
//            UserDTO user = new UserDTO(full);
//            String password = full.getPassword();

        } catch (InvalidRequestException | MismatchedInputException e) {

            // Invalid user info
            e.printStackTrace();
            resp.setStatus(400);
            ErrorResponse errResp = new ErrorResponse(400, e.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.error("Invalid user info!", e);

        } catch (ResourcePersistenceException rpe) {

            // Duplicate user info
            resp.setStatus(409);
            ErrorResponse errResp = new ErrorResponse(409, rpe.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.error("Error writing to database. This was most likely due to duplicate user information.", rpe);

        } catch (IOException ie) {

            resp.setStatus(501);
            ErrorResponse errResp = new ErrorResponse(501, ie.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.error("Error reading input stream", ie);

        } catch (Exception e) {

            resp.setStatus(500);
            ErrorResponse errResp = new ErrorResponse(500, "An unknown exception occurred.");
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.error("An unknown exception occurred.", e);
        }

    }

    /**
     *  The doPatch method is used to "patch" information, as in push only a partial update to the API, where doPut
     *  is used to replace existing information entirely.
     *  // TODO currently exact same as doPut, change to make doPatch update rather than replace
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        try {

            ServletInputStream sis = req.getInputStream();
            User user = mapper.readValue(sis, User.class);

            // Ensure user is being updated by the requesting user
            try {
                authorizeCurrentUser(req, resp, user);
            } catch (Exception e) {
                e.printStackTrace();
                respWriter.write(e.getMessage());
                return;
            }

            Principal principal = (Principal) req.getAttribute("principal");
            user.setId(principal.getId());
            UserDTO updatedUser = userService.updateUser(user);
            respWriter.write(mapper.writeValueAsString(updatedUser));
//            UserDTO user = new UserDTO(full);
//            String password = full.getPassword();

        } catch (InvalidRequestException | MismatchedInputException e) {

            // Invalid user info
            e.printStackTrace();
            resp.setStatus(400);
            ErrorResponse errResp = new ErrorResponse(400, e.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.error("Invalid user info!", e);

        } catch (ResourcePersistenceException rpe) {

            // Duplicate user info
            resp.setStatus(409);
            ErrorResponse errResp = new ErrorResponse(409, rpe.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.error("Error writing to database. This was most likely due to duplicate user information.", rpe);

        } catch (IOException ie) {

            resp.setStatus(501);
            ErrorResponse errResp = new ErrorResponse(501, ie.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.error("Error reading input stream", ie);

        } catch (Exception e) {

            resp.setStatus(500);
            ErrorResponse errResp = new ErrorResponse(500, "An unknown exception occurred.");
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.error("An unknown exception occurred.", e);
        }
    }

    /**
     *  The doDelete method is used to remove existing user information from the database. The requesting user must
     *  first be validated using the authorize method.
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");


        try {
            User user = mapper.readValue(req.getInputStream(), User.class);

            // Ensure user is being updated by the requesting user
            try {
                authorizeCurrentUser(req, resp, user);
            } catch (Exception e) {
                e.printStackTrace();
                respWriter.write(e.getMessage());
                return;
            }

            // Delete user
            userService.deleteUser(user);
            String msg = "Successfully deleted user.";
            respWriter.write(msg);
            logger.info(msg);
        } catch (Exception e) {
            resp.setStatus(500);
            ErrorResponse errResp = new ErrorResponse(500, "An unknown exception occurred.");
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.error("An unknown exception occurred.", e);
        }
    }

    /**
     * A convenience method used to abstract logic related to creating and writing a ErrorResponse to
     * the response body that will be sent back to the requester.
     *
     * @param msg Descriptive error message that will be sent to the requester
     * @param statusCode HTTP status code that will be set on the response
     * @param resp HttpServletResponse object that is used to set the status code and write to the body of the response
     * @throws IOException Occurs if there are issues with writing to the response body
     */
    public void writeErrorResponse(String msg, int statusCode, HttpServletResponse resp) throws IOException {
        resp.setStatus(statusCode);
        ErrorResponse errResp = new ErrorResponse(statusCode, msg);
        resp.getWriter().write(mapper.writeValueAsString(errResp));
    }

    /**
     * Used to determine if a provided username or email is available for use. An appropriate response is sent
     * back to indicate whether the provided value is already used by another user in the data source.
     * @param req
     * @param resp
     * @throws IOException
     */
    public void availabilityCheck(HttpServletRequest req, HttpServletResponse resp) throws IOException {

//        String usernameParam = req.getParameter("username");
//        String emailParam = req.getParameter("email");
//        boolean isAvailable;
//
//        try {
//
//            if (usernameParam != null) {
//                isAvailable = userService.isUsernameAvailable(usernameParam);
//            } else if (emailParam != null) {
//                isAvailable = userService.isEmailAvailable(emailParam);
//            } else {
//                writeErrorResponse("No values provided, cannot check availability.", 400, resp);
//                return;
//            }
//
//            if (isAvailable) {
//                resp.setStatus(200);
//                resp.getWriter().write("{\"isAvailable\": \"true\"}");
//            } else {
//                resp.setStatus(409);
//                resp.getWriter().write("{\"isAvailable\": \"false\"}");
//            }
//
//        } catch (InvalidRequestException ire) {
//            logger.info(ire.getMessage());
//            writeErrorResponse(ire.getMessage(), 400, resp);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            writeErrorResponse("An unexpected error occurred on the server.", 500, resp);
//        }

    }


}

