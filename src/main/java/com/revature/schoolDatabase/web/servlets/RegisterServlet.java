package com.revature.schoolDatabase.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.revature.schoolDatabase.datasource.models.Student;
import com.revature.schoolDatabase.datasource.models.User;
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

public class RegisterServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
    private final UserService userService;
    private final ObjectMapper mapper;

    public RegisterServlet(UserService userService, ObjectMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getAttribute("filtered"));
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");
        
        // Get the session from the request, if it exists (do not create one)
        HttpSession session = req.getSession(false);
        
        // If the session is not null, then grab the auth-user attribute from it
        Principal requestingUser = (session == null) ? null : (Principal) session.getAttribute("auth-user");
        
        // Check to see if there was a valid auth-user attribute
        if (requestingUser == null) {
            String msg = "No session found, please login.";
            logger.info(msg);
            resp.setStatus(401);
            ErrorResponse errResp = new ErrorResponse(401, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        }

        String userIDParam = req.getParameter("id");

        try {
            if (userIDParam == null) {
                // Return a list of all User DTOs
                List<UserDTO> users = userService.retrieveUsers();
                respWriter.write(mapper.writeValueAsString(users));
            } else {
                // Return User DTO corresponding to userIDParam
                UserDTO currentUser = userService.findUserById(userIDParam);
                respWriter.write(mapper.writeValueAsString(currentUser));
            }

        } catch (ResourceNotFoundException rnfe) {
            resp.setStatus(404);
            ErrorResponse errResp = new ErrorResponse(404, rnfe.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);    // server's fault
            ErrorResponse errResp = new ErrorResponse(500, "The server experienced an issue, please try again later.");
            respWriter.write(mapper.writeValueAsString(errResp));
        }

    }

    /**
     * Reads a JSON object and converts it to User Information to insert into the MongoDB
     *
     * @param req
     * @param resp
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
//            respWriter.write(newUser.toString());
//            Student newUser = new Student("username", "password", "username", "password");
            Principal principal = new Principal(userService.register(newUser));
            String payload = mapper.writeValueAsString(principal);
            respWriter.write(payload);
            resp.setStatus(201);
        } catch (InvalidRequestException | MismatchedInputException e) {
            e.printStackTrace();
            resp.setStatus(400);    // client's fault
            ErrorResponse errResp = new ErrorResponse(400, e.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch (ResourcePersistenceException rpe) {
            resp.setStatus(409);
            ErrorResponse errResp = new ErrorResponse(409, rpe.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch (IOException ie) {
            ie.printStackTrace();
            respWriter.write("Error reading input stream!");
            resp.setStatus(501);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);    // server's fault
        }
    }

}

