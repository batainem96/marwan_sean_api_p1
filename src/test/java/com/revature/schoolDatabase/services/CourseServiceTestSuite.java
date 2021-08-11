package com.revature.schoolDatabase.services;

import com.revature.schoolDatabase.models.*;
import com.revature.schoolDatabase.repositories.CourseRepository;
import com.revature.schoolDatabase.repositories.UserRepository;
import com.revature.schoolDatabase.util.exceptions.DataSourceException;
import com.revature.schoolDatabase.util.exceptions.ResourcePersistenceException;
import com.revature.schoolDatabase.util.exceptions.SchedulingException;
import org.junit.*;
import org.mockito.Mockito;
import org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
        Assert.assertEquals("Expected user to be considered invalid!", expectedResult, actualResult1);
    }


    /**
     * For this test we want to ensure that displayCourse at least prints out Title, Dept, and ID.
     * We also want other information the Course may have to display.
     */
    @Test
    public void displayCourse_ignoresMissingInformation_exceptTitleAndCourseID() {
        // Arrange
        Course newCourse = new Course("TestTitle", "TestDepartment", 101, 1);
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

    @Test(expected = ResourcePersistenceException.class)
    public void createCourse_throwsException_ifDuplicate() {
        // Arrange
        Course dupCourse = new Course("Duplicate", "Test", 101, 1);
        when(mockCourseRepo.save(dupCourse)).thenThrow(new DataSourceException("Test", new Throwable()));

        // Act
        try {
            sut.createCourse(dupCourse);
        } catch (DataSourceException dse) {
            // Assert
            verify(mockCourseRepo, times(1)).save(dupCourse);
        }
    }

    @Test
    public void showCourses_displaysNothing_withNoError_ifNoCourses() {
        // Arrange
        when(mockCourseRepo.retrieveCourses()).thenReturn(null);
        when(mockCourseRepo.retrieveInstructorCourses("Test","Test")).thenReturn(null);
        Faculty user = new Faculty("Test", "Test", "Test", "Test");

        PrintStream oldOut = System.out;
        // Create new System output stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        // Act
        sut.showCourses();
        sut.showCourses(user, "instructor");
        String output = new String(baos.toByteArray());
        System.setOut(oldOut);

        // Assert
        verify(mockCourseRepo, times(1)).retrieveCourses();
        verify(mockCourseRepo, times(1)).retrieveInstructorCourses("Test", "Test");
        assertFalse(output.contains("null"));
    }

    @Test(expected = SchedulingException.class)
    public void addCourse_throwsException_ifNoOpenSeats() {
        // Arrange
        Course course = new Course("Test", "Test", 101, 1);
        when(mockCourseRepo.findByCredentials("Test", 101, 1)).thenReturn(course);
        Student stud = new Student("Test", "Test", "Test", "Test");

        // Act
        try {
            sut.addCourse(stud, "Test", "101-1");
        } finally {
            // Assert
            assertEquals(course.getOpenSeats(), 0);
        }
    }

    @Test(expected = ResourcePersistenceException.class )
    public void updateCourse_throwsException_ifUpdateFailed() {
        // Arrange
        Course course = new Course("Test", "Test", 101, 1);
        when(mockCourseRepo.update(course)).thenReturn(false);

        // Act
        sut.updateCourse(course);

        // Assert
    }

    @Test
    public void removeCourseFromSchedule_worksAsImplied() {
        // Arrange
        Person user = new Student("Test", "Test", "Test", "Test");
        ArrayList<Schedule> newSched = new ArrayList<Schedule>();
        Schedule course = new Schedule("TEST", 101, 1);
        newSched.add(course);
        user.setSchedule(newSched);

        // Act
        Person updatedUser = sut.removeCourseFromSchedule(user, course);

        // Assert
//        assertNotEquals(user, updatedUser); Person.equals does not compare Schedules
        assertFalse(updatedUser.getSchedule().contains(course));
    }

    @Test(expected = ResourcePersistenceException.class)
    public void deleteCourse_throwsException_ifDeleteFailed() {
        // Arrange
        String dept = "Test";
        int courseNo = 101;
        int sectionNo = 1;
        when(mockCourseRepo.deleteByCredentials(dept, courseNo, sectionNo)).thenReturn(false);

        // Act
        boolean result = sut.deleteCourse(dept, courseNo, sectionNo);

        // Assert
    }
}
