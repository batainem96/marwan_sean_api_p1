package com.revature.schoolDatabase.services;

import com.revature.schoolDatabase.models.Course;
import com.revature.schoolDatabase.models.Faculty;
import com.revature.schoolDatabase.models.Person;
import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.repositories.CourseRepository;
import com.revature.schoolDatabase.repositories.UserRepository;
import org.junit.*;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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

//    @Test
//    public void isUserValid_returnsTrue_givenValidUser() {
//        // AAA - Arrange, Act, Assert
//
//        // Arrange
//        boolean expectedResult = true;
//        Person validStudent = new Student("valid", "valid", "valid", "valid");
//        Person validFaculty = new Faculty("valid", "valid", "valid", "valid");
//
//        // Act
//        boolean actualResult1 = sut.isUserValid(validStudent);
//        boolean actualResult2 = sut.isUserValid(validFaculty);
//
//        // Assert
//        Assert.assertEquals("Expected user to be considered valid!", expectedResult, actualResult1);
//        Assert.assertEquals("Expected user to be considered valid!", expectedResult, actualResult2);
//    }

    @Test
    public void displayCourse_printsDeptShort_orPrintsDepartment() {

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
