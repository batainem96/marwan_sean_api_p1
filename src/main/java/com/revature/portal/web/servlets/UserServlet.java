package com.revature.portal.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import com.revature.portal.datasource.models.Student;
import com.revature.portal.services.UserService;
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

        // Get the principal information from the request, if it exists.
        Principal requestingUser = (Principal) req.getAttribute("principal");

        //------------------------------------------------------------------------------------------

        /*
            This segment checks to see if the requester is trying to check the availability of a username
            or email address. We do this by checking to see if "/availability" was included on the end
            of the request URI.
         */


        boolean checkingAvailability = reqFrags[reqFrags.length - 1].equals("availability");

        if (checkingAvailability) {
            availabilityCheck(req, resp);
            return; // end here, do not proceed to the remainder of the method's logic
        }

        //------------------------------------------------------------------------------------------

        /*
            This segment checks to see if the requesting user is authenticated and has the proper
            permissions to perform the action. This logic is what secures our endpoint and prevents
            unauthorized access and usage.
         */

        // Check to see if there was a valid principal attribute
        if (requestingUser == null) {
            String msg = "No session found, please login.";
            logger.info(msg);
            writeErrorResponse(msg, 401, resp);
            return; // end here, do not proceed to the remainder of the method's logic
        } else if (!requestingUser.getUsername().equals("ssmith")) {
            String msg = "Unauthorized attempt to access endpoint made by: " + requestingUser.getUsername();
            writeErrorResponse(msg, 403, resp);
            logger.info(msg);
            return; // end here, do not proceed to the remainder of the method's logic
        }

        //------------------------------------------------------------------------------------------

        /*
            This segment is what performs the searching logic for retrieving users based on the request.
            If the user included a request parameter called "id", we will simply search the data source
            for a single user with the provided id. Otherwise, we will assume that they intend to retrieve
            all the users from the data source.
         */



        try {

            if (idParam == null) {
                List<UserDTO> users = userService.retrieveUsers();
                respWriter.write(mapper.writeValueAsString(users));
            } else {
                UserDTO user = userService.findUserById(idParam);
                respWriter.write(mapper.writeValueAsString(user));
            }

        } catch (ResourceNotFoundException rnfe) {
            logger.info(rnfe.getMessage());
            writeErrorResponse(rnfe.getMessage(), 404, resp);
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

        System.out.println(req.getAttribute("filtered"));
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        try {

            // TODO Read input as either Student or Faculty
            ServletInputStream sis = req.getInputStream();
            Student newUser = mapper.readValue(sis, Student.class);
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

