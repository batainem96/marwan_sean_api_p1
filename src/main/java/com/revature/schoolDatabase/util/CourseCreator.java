package com.revature.schoolDatabase.util;

import com.revature.schoolDatabase.models.Course;
import com.revature.schoolDatabase.models.MeetingTime;
import com.revature.schoolDatabase.models.PreReq;
import com.revature.schoolDatabase.services.CourseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.*;

public class CourseCreator{
    private static final Logger logger = LogManager.getLogger(CourseCreator.class);

    /**
     * Creates a new course for the catalog given course information
     *
     * @return
     * @throws IOException
     */
    public static Course createCourse(BufferedReader consoleReader) throws IOException {
        System.out.println("Create a new course!");

        // Necessary course info, nothing else is absolutely necessary
        System.out.print("Enter course title:\n> ");
        String title = validateString(consoleReader.readLine());
        System.out.print("Enter course department:\n> ");
        String dept = validateString(consoleReader.readLine());
        System.out.print("Enter course number:\n> ");
        int courseNo = validateInt(consoleReader.readLine());
        System.out.print("Enter section number:\n> ");
        int sectionNo = validateInt(consoleReader.readLine());

        System.out.print("Any prerequisites? (y or n):\n> ");
        String choice = "";
        ArrayList<PreReq> preReqs = null;
        do {
            choice = validateString(consoleReader.readLine()).toLowerCase();
            switch (choice) {
                case "y":
                    preReqs = createPreReqs(consoleReader);
                    break;
                case "n":
                    break;
                default:
                    System.out.print("Invalid selection, try again.\n> ");
                    break;
            }
        } while (!(choice.equals("y")) && !(choice.equals("n")));

        System.out.print("Enter instructor name:\n> ");
        String instructor = validateString(consoleReader.readLine());

        System.out.print("Enter number of credits:\n> ");
        int credits = validateInt(consoleReader.readLine());

        System.out.print("Enter total number of seats:\n> ");
        int totalSeats = validateInt(consoleReader.readLine());

        System.out.print("Any meeting times? (y or n):\n> ");
        ArrayList<MeetingTime> meetingTimes = null;
        do {
            choice = validateString(consoleReader.readLine()).toLowerCase();
            switch (choice) {
                case "y":
                    meetingTimes = createMeetingTimes(consoleReader);
                    break;
                case "n":
                    break;
                default:
                    System.out.print("Invalid selection, try again.\n> ");
                    break;
            }
        } while (!(choice.equals("y")) && !(choice.equals("n")));

        System.out.print("Enter course description:\n> ");
        String desc = validateString(consoleReader.readLine());

        Course newCourse = new Course(title, dept, courseNo, sectionNo, preReqs, instructor, credits, totalSeats, meetingTimes, desc);

        return newCourse;
    }

    public static ArrayList<PreReq> createPreReqs(BufferedReader consoleReader) throws IOException {
        // TODO Validate PreReqs more extensively
        ArrayList<PreReq> preReqs = new ArrayList<>();
        String input = "";
        while (!(input.equalsIgnoreCase("q"))) {
            PreReq newPreReq = new PreReq();
            System.out.println("Enter next prerequisite (Type 'q' to exit:\n");
            System.out.print("Enter department short hand:\n> ");
            input = validateString(consoleReader.readLine());
            if (input.equalsIgnoreCase("q"))
                break;
            newPreReq.setDepartment(input);

            System.out.print("Enter course number:\n> ");
            input = validateString(consoleReader.readLine());
            if (input.equalsIgnoreCase("q"))
                break;
            newPreReq.setCourseNo(validateInt(input));

            System.out.print("Enter number of credits:\n> ");
            input = validateString(consoleReader.readLine());
            if (input.equalsIgnoreCase("q"))
                break;
            newPreReq.setCredits(validateInt(input));

            preReqs.add(newPreReq);
        }
        return preReqs;
    }

    public static ArrayList<MeetingTime> createMeetingTimes(BufferedReader consoleReader) throws IOException {
        // TODO Validate Meeting Times more extensively
        ArrayList<MeetingTime> meetingTimes = new ArrayList<>();
        String input = "";
        while (!(input.equalsIgnoreCase("q"))) {
            MeetingTime newMeetingTime = new MeetingTime();
            System.out.println("Enter next meeting time (Type 'q' to exit:\n");
            System.out.print("Enter meeting day:\n> ");
            input = validateString(consoleReader.readLine());
            if (input.equalsIgnoreCase("q"))
                break;
            newMeetingTime.setDay(input);

            System.out.print("Enter start time (military time):\n> ");
            input = validateString(consoleReader.readLine());
            if (input.equalsIgnoreCase("q"))
                break;
            newMeetingTime.setStartTime(validateInt(input));

            System.out.print("Enter end time (military time):\n> ");
            input = validateString(consoleReader.readLine());
            if (input.equalsIgnoreCase("q"))
                break;
            newMeetingTime.setEndTime(validateInt(input));

            System.out.print("Enter class type:\n> ");
            input = validateString(consoleReader.readLine());
            if (input.equalsIgnoreCase("q"))
                break;
            newMeetingTime.setClassType(input);

            meetingTimes.add(newMeetingTime);
        }
        return meetingTimes;
    }

    /**
     * Returns the string if it is valid input, otherwise, return null
     *
     * @param str
     * @return
     */
    public static String validateString(String str) {
        if (str == null)
            return "";
        Pattern pattern = Pattern.compile("^[A-Za-z, 0-9]++$");
        if (!pattern.matcher(str).matches())
            return "";
        else return str;
    }

    public static int validateInt(String str) {
        if (str == null)
            return 0;
        try {
            int result = Integer.parseInt(str);
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0;
        }
    }
}
