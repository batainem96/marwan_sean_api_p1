package com.revature.schoolDatabase.services;

import com.mongodb.client.MongoClient;
import com.revature.schoolDatabase.models.*;
import com.revature.schoolDatabase.repositories.CourseRepository;
import com.revature.schoolDatabase.util.exceptions.DataSourceException;
import com.revature.schoolDatabase.util.exceptions.ResourcePersistenceException;
import com.revature.schoolDatabase.util.exceptions.SchedulingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CourseService {
    // Variables
    private final CourseRepository courseRepo;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public CourseService(CourseRepository courseRepo) {
        this.courseRepo = courseRepo;
    }

    /**
     * Checks fields of given course to ensure the information given is safe to store in database.
     *
     * @param course
     * @return
     */
    public boolean isCourseValid(Course course) {
        // Ensure course has title, department, courseNo, and sectionNo
        if (course == null) return false;
        if (course.getTitle() == null || course.getTitle().trim().equals("")) return false;
        if (course.getDepartment() == null || course.getDepartment().trim().equals("")) return false;
        if (course.getCourseNo() == 0) return false;
        if (course.getSectionNo() == 0) return false;

        // TODO Other info is not a priority
        return true;
    }

    public Faculty generateSchedule(Faculty fac) {
        List<Course> courseList = courseRepo.retrieveInstructorCourses(fac.getFirstName(), fac.getLastName());
        if (courseList == null)
            return fac;

        // Add to Faculty schedule if the courses do not exist
        for (Course course : courseList) {
            String dept = course.getDeptShort();
            int courseNo = course.getCourseNo();
            int sectionNo = course.getSectionNo();
            ArrayList<MeetingTime> meetingTimes = course.getMeetingTimes();
            Schedule newSched = new Schedule(dept, courseNo, sectionNo, meetingTimes);

            if (!fac.getSchedule().contains(newSched))
                fac.getSchedule().add(newSched);
//            course.displayShortCourse();
        }

        return fac;
    }


    /**
     * Stores a new course into the database
     *
     * @param newCourse
     */
    public void createCourse(Course newCourse) {
        try {
            courseRepo.save(newCourse);
        } catch (DataSourceException dse) {
            throw new ResourcePersistenceException("An error occurred while calling CourseRepository.save()");
        }

    }

    /**
     * Lists all courses in catalog
     */
    public void showCourses() {
        List<Course> courseList = courseRepo.retrieveCourses();
        if (courseList == null)
            return;
        for (Course course : courseList) {
            course.displayCourse();
        }
    }

    /**
     * Lists courses in catalog according to flags
     *
     * @param flags -- used to specify which courses to display
     *             List of flags:
     *                  -- 'open' = courses with open seats
     *                  -- 'closed' = courses which have no open seats
     *                  -- 'user' = courses currently in a given Person's schedule
     *                  -- 'schedule' = courses that would fit in schedule TODO
     *                  -- 'dept' = courses in a given Department
     *                  -- 'short' = display only pertinent Course information
     *                  -- 'instructor' = courses taught by given user
     */
    public void showCourses(Person user, String flag) {
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
     *
     * @param stud
     * @param courseID
     */
    public Person addCourse(Student stud, String dept, String courseID) {
        // TODO -------------------------------

        // Find course in database given courseID
        String[] splitID = courseID.split("-");
        Course newCourse = courseRepo.findByCredentials(dept, Integer.parseInt(splitID[0]), Integer.parseInt(splitID[1]));

        // Check if course has open seats
        if ((newCourse.getOpenSeats() == 0))
            throw new SchedulingException("Class has no open seats!");

        String newDeptShort = newCourse.getDeptShort();
        int newCourseNo = newCourse.getCourseNo();
        int newSectionNo = newCourse.getSectionNo();
        ArrayList<MeetingTime> newMeetingTimes = newCourse.getMeetingTimes();

        Schedule courseData = new Schedule(newDeptShort, newCourseNo, newSectionNo, newMeetingTimes);
        try {
            stud.getSchedule().add(courseData);
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
    public void updateCourse(Course course) {
        boolean result = courseRepo.update(course);
        if (!result)
            throw new ResourcePersistenceException("Failed to update course");
    }

    /**
     * Removes given course from given user's schedule
     *
     * @param user
     * @param course
     * @return
     */
    public Person removeCourseFromSchedule(Person user, Schedule course) {
        for (Schedule existingCourse : user.getSchedule()) {
            if (existingCourse.equals(course)) {
                Person updatedUser = user;
                updatedUser.getSchedule().remove(existingCourse);
                return updatedUser;
            }
        }
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
}
