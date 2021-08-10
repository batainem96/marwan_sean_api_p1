package com.revature.schoolDatabase.services;

import com.mongodb.client.MongoClient;
import com.revature.schoolDatabase.models.*;
import com.revature.schoolDatabase.repositories.CourseRepository;
import com.revature.schoolDatabase.util.exceptions.DataSourceException;

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
     * Lists all courses in catalog
     */
    public void showCourses() {
        List<Course> courseList = courseRepo.retrieveCourses();
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
    public void showCourses(Person user, String... flags) {
        if (Arrays.asList(flags).contains("instructor")) {
            List<Course> courseList = courseRepo.retrieveInstructorCourses(user.getFirstName(), user.getLastName());
            // Add to Faculty schedule if the courses do not exist
            for (Course course : courseList) {
                String dept = course.getDeptShort();
                int courseNo = course.getCourseNo();
                int sectionNo = course.getSectionNo();
                ArrayList<MeetingTime> meetingTimes = course.getMeetingTimes();
                Schedule newSched = new Schedule(dept, courseNo, sectionNo, meetingTimes);

                if (!user.getSchedule().contains(newSched))
                    user.getSchedule().add(newSched);
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
        String newDeptShort = newCourse.getDeptShort();
        int newCourseNo = newCourse.getCourseNo();
        int newSectionNo = newCourse.getSectionNo();
        ArrayList<MeetingTime> newMeetingTimes = newCourse.getMeetingTimes();

        Schedule courseData = new Schedule(newDeptShort, newCourseNo, newSectionNo, newMeetingTimes);
        stud.getSchedule().add(courseData);
        // Return reference to student to be updated
        return stud;

        // TODO Compare new course info with student (schedule, etc) to ensure the add is valid

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
            throw new DataSourceException("An error occurred while calling CourseRepository.save()", dse);
        }

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
        Course course = courseRepo.findByCredentials(dept, courseNo, sectionNo);
        return course;
    }

    public Course findCourseByID(String id) {
        Course course = courseRepo.findById(id);
        return course;
    }

    public void updateCourse(Course course) {
        courseRepo.update(course);
    }

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
     * Deletes a course from the database
     */
    public boolean deleteCourse(String dept, int courseNo, int sectionNo) {
        boolean result = courseRepo.deleteByCredentials(dept, courseNo, sectionNo);


        return result;

        // TODO remove Course reference from applicable schedules
    }
}
