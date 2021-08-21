package com.revature.schoolDatabase.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.schoolDatabase.datasource.models.Course;
import com.revature.schoolDatabase.services.CourseService;
import com.revature.schoolDatabase.util.exceptions.InvalidRequestException;
import com.revature.schoolDatabase.util.exceptions.ResourcePersistenceException;
import com.revature.schoolDatabase.web.dtos.ErrorResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The AddCourseServlet class processes course creation requests from the web application.
 */
public class AddCourseServlet extends HttpServlet {

    private final CourseService courseService;
    private final ObjectMapper mapper;

    public AddCourseServlet(CourseService courseService, ObjectMapper mapper) {
        this.courseService = courseService;
        this.mapper = mapper;
    }

    /**
     * The doPost method accepts a POST request and forwards the message body contents, which contain fields describing
     * a course (which need to match up with the Course class's fields). The course is then forwarded to a CourseService
     * method which may return a copy of the Course object, or throw some exception. An InvalidRequestException
     * indicates faulty input from the web application, and a 400 error code is returned. A ResourcePersistenceException
     * suggests that there is an issue with the server/DB connection.
     *
     * @param req - HttpServletRequest object.
     * @param resp - HttpServletResponse object.
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Instantiate response writer object
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        try {

            // Map request message body to a Course object
            Course newCourse = mapper.readValue(req.getInputStream(), Course.class);

            // TODO: currently doing nothing by setting newCourse equal to the return of createCourse(newCourse)
            //  should we keep this logic?
            newCourse = courseService.createCourse(newCourse);

            // Send response: course information as JSON object
            String payload = mapper.writeValueAsString(newCourse);
            respWriter.write(payload);

        } catch(InvalidRequestException ire) {
            resp.setStatus(400); // Bad request error status
            ErrorResponse errResp = new ErrorResponse(400, ire.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch (ResourcePersistenceException rpe) {
            resp.setStatus(500); // Internal server error status
            ErrorResponse errResp = new ErrorResponse(400, rpe.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        }

    }
}
