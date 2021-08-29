package com.revature.portal.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.portal.datasource.models.*;
import com.revature.portal.datasource.repositories.CourseRepository;
import com.revature.portal.datasource.repositories.UserRepository;
import com.revature.portal.util.exceptions.*;
import com.revature.portal.datasource.models.Course;
import com.revature.portal.web.dtos.CourseHeader;


import java.util.ArrayList;
import java.util.List;

/**
 * The CourseService class provides a service abstraction layer between the application layer and database connection
 * layer for queries on the courses collection. This service layer provides business logic validation and protects
 * against malicious user input, and facilitates transactions between the application and database layers.
 *
 * Authors: Sean Dunn, Marwan Bataineh
 * Date: 19 August 2021
 * Last Modified: 19 August 2021
 */
public class CourseService {
    // Variables
    private final CourseRepository courseRepo;

    public CourseService(CourseRepository courseRepo) {
        this.courseRepo = courseRepo;
    }

    /**
     * The isCourseValid method accepts a Course object and validates its fields. Method checks for: object validity
     * (not null), title and department fields are not null nor empty, and course and section numbers are not invalid
     * numbers (zero or negative are not valid).
     *
     * @param course - The Course object containing course information whose fields need to be validated.
     * @return - Returns true if the course fields passed all validity checks; false if one or more validity checks did
     *  not pass.
     */
    public boolean isCourseValid(Course course) {
        // Ensure course has title, department, courseNo, and sectionNo
        if (course == null) return false;
        if (course.getTitle() == null || course.getTitle().trim().equals("")) return false;
        if (course.getDepartment() == null || course.getDepartment().trim().equals("")) return false;
        if (course.getCourseNo() < 1) return false;
        if (course.getSectionNo() < 1) return false;

        // TODO Other info is not a priority
        return true;
    }

    /**
     *
     * @param fac
     * @return
     */
    public Faculty generateSchedule(Faculty fac) {
        if (fac == null) return fac;

        fac.setSchedule((ArrayList<Course>) courseRepo.retrieveInstructorCourses(fac.getFirstName(), fac.getLastName()));

        return fac;
    }


    /**
     * Stores a new course into the database
     *
     * @param newCourse
     */
    public Course createCourse(Course newCourse) {

        if(!isCourseValid(newCourse)) {
            throw new InvalidRequestException("One or more Course fields are not valid.");
        }

        try {
            return courseRepo.save(newCourse);
        } catch (DataSourceException dse) {
            throw new ResourcePersistenceException("An error occurred while calling CourseRepository.save().");
        }

    }

    /**
     * Returns all courses in catalog
     */
    public List<Course> retrieveCourses() {
        return courseRepo.retrieveCourses();
    }

    /**
     * Lists courses in catalog according to flags
     *
     * @param flag -- used to specify which courses to display
     *             List of flags:
     *                  -- 'open' = courses with open seats
     *                  -- 'closed' = courses which have no open seats TODO
     *                  -- 'user' = courses currently in a given Person's schedule TODO
     *                  -- 'schedule' = courses that would fit in schedule TODO
     *                  -- 'dept' = courses in a given Department TODO
     *                  -- 'short' = display only pertinent Course information TODO
     */
    public void showCourses(User user, String flag) {
        if (flag.equals("instructor")) {
            List<Course> courseList = courseRepo.retrieveInstructorCourses(user.getFirstName(), user.getLastName());
            if (courseList == null)
                return;

            for (Course course : courseList) {
                course.displayShortCourse();
            }
        }
        else if (flag.equals("open")) {
            List<Course> courseList = courseRepo.retrieveOpenCourses();
            if (courseList == null)
                return;

            for (Course course : courseList) {
                course.displayShortCourse();
            }
        }
        else {
            List<Course> courseList = courseRepo.retrieveCourses();
            for (Course course : courseList) {
                course.displayShortCourse();
            }
        }
    }

    /**
     * Adds a new course to a Student's schedule
     * TODO: This is not functional
     *
     * @param stud
     */
    public User addCourse(Student stud, Course course) {

        // Find course in database given courseID
        Course newCourse = courseRepo.findByCredentials(course.getDeptShort(), course.getCourseNo(), course.getSectionNo());

        if(newCourse == null) {
            throw new ResourceNotFoundException("Course not found!");
        }

        // Check if course has open seats
        if ((newCourse.getOpenSeats() == 0))
            throw new SchedulingException("Class has no open seats!");

        try {
            stud.getSchedule().add(newCourse);
            newCourse.setOpenSeats(newCourse.getOpenSeats() - 1);
        } catch (Exception e) {
            throw new DataSourceException("Error adding course to schedule", e);
        }
        // Return reference to student to be updated
        return stud;

        // TODO Compare new course info with student (schedule, etc) to ensure the add is valid

    }

    /**
     * Find course in database with given credentials
     *
     * @param dept
     * @param courseNo
     * @param sectionNo
     * @return
     */
    public Course findCourseByCredentials(String dept, int courseNo, int sectionNo) {
        return courseRepo.findByCredentials(dept, courseNo, sectionNo);
    }

    /**
     * Finds course in database with an Object ID matching given String.
     *
     * @param id
     * @return
     */
    public Course findCourseByID(String id) {
        Course course = courseRepo.findById(id);
        return course;
    }

    /**
     * Persist any changes made to Course to the database.
     *
     * @param course
     */
    public void replaceCourse(Course course) {
        if (!isCourseValid(course))
            throw new InvalidRequestException("Course is not valid!");
        boolean result = courseRepo.replace(course);
        if (!result)
            throw new ResourcePersistenceException("Failed to update course");
    }

    /**
     * Persist any changes made to Course to the database.
     *
     */
    public Course updateCourse(Course course) {
        Course updatedCourse = courseRepo.update(course);
        return updatedCourse;
    }

    /**
     * Adds given course to a user's schedule.
     * Different from addCourse in that this adds a Schedule object directly to a Schedule list
     *
     * @param user
     * @param course
     * @return
     */
    public User addCourseToSchedule(User user, Course course) {
//        List<Schedule> schedule = user.getSchedule();
//        for (Schedule existingCourse : schedule) {
//            if (existingCourse.equals(course)) {
//                Person updatedUser = user;
//                updatedUser.getSchedule().remove(existingCourse);
//                return updatedUser;
//            }
//        }

        user.addToSchedule(course);

        return user;
    }

    /**
     * Removes given course from given user's schedule
     *
     * @param user
     * @param course
     * @return
     */
    public User removeCourseFromSchedule(User user, Course course) {
//        List<Schedule> schedule = user.getSchedule();
//        for (Schedule existingCourse : schedule) {
//            if (existingCourse.equals(course)) {
//                Person updatedUser = user;
//                updatedUser.getSchedule().remove(existingCourse);
//                return updatedUser;
//            }
//        }

        boolean result = user.removeFromSchedule(course);

        // If course was not found in schedule, return original user
        return user;
    }

    /**
     * Deletes a course from the database with the given credentials
     */
    public boolean deleteCourse(String dept, int courseNo, int sectionNo) {
        boolean result = courseRepo.deleteByCredentials(dept, courseNo, sectionNo);
        if (!result)
            throw new ResourcePersistenceException("Failed to delete course");
        else return result;
    }

    /**
     *  Deletes a course from the database with the given id.
     */
    public boolean deleteCourseByID(String id) {
        UserRepository userRepo = new UserRepository(new ObjectMapper().findAndRegisterModules());
        boolean result = courseRepo.deleteById(id);
        boolean result2 = userRepo.deleteCourseByIdFromUsers(id);
        if (!result || !result2)
            throw new ResourcePersistenceException("No course was found with given id.");
        else return true;
    }
}
