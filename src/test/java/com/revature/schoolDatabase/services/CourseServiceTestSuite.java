package com.revature.schoolDatabase.services;

import com.revature.schoolDatabase.models.*;
import com.revature.schoolDatabase.repositories.CourseRepository;
import com.revature.schoolDatabase.repositories.UserRepository;
import org.junit.*;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class CourseServiceTestSuite {
    CourseService sut; // SUT = System Under Test (the thing being tested)

    private CourseRepository mockCourseRepo;

    /*
        Common JUnit 4 Annotations

            - @BeforeClass
            - @AfterClass
            - @Before
            - @After
            - @Test
            - @Ignore
     */

    // TODO spoof database access
    @BeforeClass // runs before all test cases; runs only once
    public static void setUpSuite() {

    }

    @AfterClass // runs after all test cases; runs only once
    public static void tearDownSuite() {

    }

    @Before // runs before each test case
    public void beforeEachTest() {
        mockCourseRepo = Mockito.mock(CourseRepository.class);
        sut = new CourseService(mockCourseRepo);
    }

    @After // runs after each test case
    public void afterEachTest() {
        sut = null;
    }

    @Test
    public void isCourseValid_returnsTrue_givenValidCourse() {
        // AAA - Arrange, Act, Assert

        // Arrange
        boolean expectedResult = true;
        Course validCourse = new Course("Test", "Test", 101, 1);

        ArrayList<PreReq> prereqs = new ArrayList<>();
        prereqs.add(new PreReq("Test", 101, 1));
        ArrayList<MeetingTime> meetingTimes = new ArrayList<>();
        meetingTimes.add(new MeetingTime("Mon", 100, 200, "Lecture"));

        Course validCourse2 = new Course("Test", "Test", 101, 1,
                prereqs, "Test", 0, 0, meetingTimes,"Test");
        Course validCourse3 = new Course("Test", "Test", 101, 1,
                null, null, 0, 0, null,null);

        // Act
        boolean actualResult1 = sut.isCourseValid(validCourse);
        boolean actualResult2 = sut.isCourseValid(validCourse2);
        boolean actualResult3 = sut.isCourseValid(validCourse3);

        // Assert
        Assert.assertEquals("Expected user to be considered valid!", expectedResult, actualResult1);
        Assert.assertEquals("Expected user to be considered valid!", expectedResult, actualResult2);
        Assert.assertEquals("Expected user to be considered valid!", expectedResult, actualResult3);
    }

    @Test
    public void isCourseValid_returnsFalse_givenInvalidCourse() {
        // Arrange
        boolean expectedResult = false;
        Course inValidCourse = new Course(null, null, 0, 0);

        // Act
        boolean actualResult1 = sut.isCourseValid(inValidCourse);

        // Assert
        Assert.assertEquals("Expected user to be considered valid!", expectedResult, actualResult1);
    }


    /**
     * For this test we want to ensure that displayCourse at least prints out Title, Dept, and ID.
     * We also want other information the Course may have to display.
     */
    @Test
    public void displayCourse_ignoresMissingInformation_exceptTitleAndCourseID() {
        // Arrange
        Course newCourse = new Course("TestTitle", "TestDepartment", 101, 1);
        // TODO Test copy of newCourse with additional info
        PrintStream oldOut = System.out;
        // Create new System output stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        // Act
        newCourse.displayCourse();
        String output = new String(baos.toByteArray());
        System.setOut(oldOut);

        // Assert
        assertTrue(output.contains("TestTitle"));
        assertTrue(output.contains("TEST"));
        assertTrue(output.contains("101-1"));
        newCourse.displayCourse();
    }
}
