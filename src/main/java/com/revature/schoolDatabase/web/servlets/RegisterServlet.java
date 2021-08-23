package com.revature.schoolDatabase.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.revature.schoolDatabase.datasource.models.Student;
import com.revature.schoolDatabase.services.UserService;
import com.revature.schoolDatabase.util.exceptions.InvalidRequestException;
import com.revature.schoolDatabase.util.exceptions.ResourceNotFoundException;
import com.revature.schoolDatabase.util.exceptions.ResourcePersistenceException;
import com.revature.schoolDatabase.web.dtos.ErrorResponse;
import com.revature.schoolDatabase.web.dtos.Principal;
import com.revature.schoolDatabase.web.dtos.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * The RegisterServlet HttpServlet class processes register requests from the web application.
 *
 * Date: 19 August 2021
 * Last Modified: 23 August 2021
 */
public class RegisterServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
    private final UserService userService;
    private final ObjectMapper mapper;

    public RegisterServlet(UserService userService, ObjectMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
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
        } catch (InvalidRequestException | MismatchedInputException e) {
            // Invalid user info
            e.printStackTrace();
            resp.setStatus(400);
            ErrorResponse errResp = new ErrorResponse(400, e.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch (ResourcePersistenceException rpe) {
            // Duplicate user info
            resp.setStatus(409);
            ErrorResponse errResp = new ErrorResponse(409, rpe.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch (IOException ie) {
            ie.printStackTrace();
            respWriter.write("Error reading input stream!");
            resp.setStatus(501);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
        }
    }

}

