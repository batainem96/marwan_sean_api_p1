package com.revature.portal.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.portal.datasource.models.Course;
import com.revature.portal.services.CourseService;
import com.revature.portal.util.exceptions.InvalidRequestException;
import com.revature.portal.util.exceptions.ResourcePersistenceException;
import com.revature.portal.web.dtos.ErrorResponse;
import com.revature.portal.web.dtos.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
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
     *  Service function to convert a request input stream to a readable string.
     *
     *  Credit: https://stackoverflow.com/questions/12260540/how-to-convert-an-httpservletrequest-to-string
     *
     * @param request
     * @return
     * @throws Exception
     */
    String httpServletRequestToString(HttpServletRequest request) throws Exception {

        ServletInputStream mServletInputStream = request.getInputStream();
        byte[] httpInData = new byte[request.getContentLength()];
        int retVal = -1;
        StringBuilder stringBuilder = new StringBuilder();

        while ((retVal = mServletInputStream.read(httpInData)) != -1) {
            for (int i = 0; i < retVal; i++) {
                stringBuilder.append(Character.toString((char) httpInData[i]));
            }
        }

        return stringBuilder.toString();
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

    /**
     *  The doDelete method takes in a HttpServletRequest with a parameter in the uri specifying the id of the course
     *  to be deleted. As this method is used to remove entries from the database, proper authorization in the form
     *  of a JWT is required in the header. This authorization is picked up by AuthFilter, which in turn, generates a
     *  Principal object out of the token and provides it for doDelete to read.
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Instantiate response writer object
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        // Get the principal information from the request, if it exists.
        Principal requestingUser = (Principal) req.getAttribute("principal");

        // Check to see if there was a valid principal attribute
        if (requestingUser == null) {
            String msg = "No session found, please login.";
            ErrorResponse errResp = new ErrorResponse(401, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.info(msg);
            return; // end here, do not proceed to the remainder of the method's logic
            // TODO Change this to check for a faculty userType
        } else if (!requestingUser.getUsername().equals("wsingleton")) {
            String msg = "Unauthorized attempt to access endpoint made by: " + requestingUser.getUsername();
            ErrorResponse errResp = new ErrorResponse(403, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.info(msg);
            return; // end here, do not proceed to the remainder of the method's logic
        }

        String idParam = req.getParameter("id");

        try {
            courseService.deleteCourseByID(idParam);
            String msg = "Successfully deleted Course, ID: " + idParam;
            logger.info(msg);
            respWriter.write(msg);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = "Failed to delete course, ID: " + idParam;
            ErrorResponse errResp = new ErrorResponse(500, msg);
            respWriter.write(mapper.writeValueAsString(errResp));
            logger.info(msg);
        }
    }
  
    /**
     *  The doPatch method takes in partial information in the form of an HttpServletRequest, in order to partially
     *  update a given course. Attributes of Course that are not explicitly given in the request are initialized as
     *  either null or -1, and only the valid fields will be pushed in an update to the database.
     *
     *  //TODO require authorization
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    protected void doPatch(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // Instantiate response writer object
        PrintWriter respWriter = res.getWriter();
        res.setContentType("application/json");

        try {
            // Map request message body to a Course object
            Course updateCourse = mapper.readValue(req.getInputStream(), Course.class);
            // Update Course
            updateCourse = courseService.updateCourse(updateCourse);
            // Respond with updated course back to sender
            respWriter.write(mapper.writeValueAsString(courseService.findCourseByID(updateCourse.getId())));
        } catch (Exception e) {
            res.setStatus(500); // Internal server error status
            ErrorResponse errResp = new ErrorResponse(500, e.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        }
    }
}
