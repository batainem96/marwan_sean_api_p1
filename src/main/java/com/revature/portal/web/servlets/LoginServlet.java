package com.revature.portal.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.portal.web.util.security.TokenGenerator;
import com.revature.portal.services.UserService;
import com.revature.portal.util.exceptions.AuthenticationException;
import com.revature.portal.web.dtos.Credentials;
import com.revature.portal.web.dtos.ErrorResponse;
import com.revature.portal.web.dtos.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The LoginServlet HttpServlet class processes login requests from the web application.
 *
 * Date: 19 August 2021
 * Last Modified: 25 August 2021
 */
public class LoginServlet extends HttpServlet {

    private final UserService userService;
    private final ObjectMapper mapper;
    private final TokenGenerator tokenGenerator;

    public LoginServlet(UserService userService, ObjectMapper mapper, TokenGenerator tokenGenerator) {
        this.userService = userService;
        this.mapper = mapper;
        this.tokenGenerator = tokenGenerator;
    }


    /**
     * The doPost method accepts a POST request and forwards the message body contents, which are expected to be
     * {username: "<username>", password: "<password>"}, to a service class as a java object (DTO) representing the
     * inputted credentials. This method expects a "principal" java object to be returned (containing user information
     * for further use in the web application); OR may catch a propagated exception which either indicates faulty
     * credentials (AuthenticationException) and responds with a 401 error code, or some other exception indicating some
     * server issue(s) and responds with a 500 error code.
     *
     * @param req - HttpServletRequest object.
     * @param resp - HttpServletResponse object.
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Instantiate response writer object
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        try {

            // Map request message body to a Credentials object
            Credentials creds = mapper.readValue(req.getInputStream(), Credentials.class);

            // On success: instantiate a Principal object with user information from the database
            Principal principal = userService.login(creds.getUsername(), creds.getPassword());

            // Send response: user information as JSON object
            String payload = mapper.writeValueAsString(principal);
            respWriter.write(payload);

            // Establish a session with user
            String token = tokenGenerator.createToken(principal);
            resp.setHeader(tokenGenerator.getJwtConfig().getHeader(), token);

        } catch (AuthenticationException ae) {
            resp.setStatus(401); // Unauthorized client error status
            ErrorResponse errResp = new ErrorResponse(401, ae.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        }  catch (Exception e) {
            e.printStackTrace(); // --- debug
            resp.setStatus(500); // Internal server error status
            ErrorResponse errResp = new ErrorResponse(500, "The server experienced an issue, please try again later.");
            respWriter.write(mapper.writeValueAsString(errResp));
        }

    }
}
