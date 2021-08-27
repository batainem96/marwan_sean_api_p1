package com.revature.portal.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.portal.datasource.models.Course;
import com.revature.portal.services.CourseService;
import com.revature.portal.util.exceptions.InvalidRequestException;
import com.revature.portal.util.exceptions.ResourcePersistenceException;
import com.revature.portal.web.dtos.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * The AddCourseServlet class processes course creation requests from the web application.
 */
public class CourseServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(CourseServlet.class);
    private final CourseService courseService;
    private final ObjectMapper mapper;

    public CourseServlet(CourseService courseService, ObjectMapper mapper) {
        this.courseService = courseService;
        this.mapper = mapper;
    }

    /**
     *  The doGet method accepts a GET request and retrieves course(s) from the database, dependent on request
     *  parameters. The 'id' parameter is used to specify which single course is to be retrieved from the database. If
     *  there are no parameters, it is assumed that the method will return all courses in the database.
     *  // TODO dept param, instructor param
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");
        String payload;

        String idParam = req.getParameter("id");
        if (idParam != null) {
            try {
                Course course = courseService.findCourseByID(idParam);
                payload = mapper.writeValueAsString(course);
            } catch (Exception e) {
                e.printStackTrace();
                ErrorResponse errResp = new ErrorResponse(400, "No course found with that id.");
                logger.error(e.getMessage(), e);
                payload = mapper.writeValueAsString(errResp);
            }
        }
        else {
            try {
                List<Course> courses = courseService.retrieveCourses();
                payload = mapper.writeValueAsString(courses);
            } catch (Exception e) {
                e.printStackTrace();
                ErrorResponse errResp = new ErrorResponse(400, "No courses found.");
                logger.error(e.getMessage(), e);
                payload = mapper.writeValueAsString(errResp);
            }
        }
        respWriter.write(payload);

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
            System.out.println(newCourse);
            newCourse.reinitializeVariables();
            System.out.println(newCourse);

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
