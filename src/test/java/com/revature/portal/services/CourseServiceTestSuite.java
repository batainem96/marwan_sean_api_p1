package com.revature.portal.services;

import com.revature.portal.datasource.models.*;
import com.revature.portal.datasource.repositories.CourseRepository;
import com.revature.portal.util.exceptions.DataSourceException;
import com.revature.portal.util.exceptions.ResourcePersistenceException;
import com.revature.portal.util.exceptions.SchedulingException;
import com.revature.portal.datasource.models.Course;
import com.revature.portal.web.dtos.CourseHeader;
import org.junit.*;
import org.mockito.Mockito;

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

    @Test(expected = ResourcePersistenceException.class )
    public void updateCourse_throwsException_ifUpdateFailed() {
        // Arrange
        Course course = new Course("Test", "Test", 101, 1);
        when(mockCourseRepo.replace(course)).thenReturn(false);

        // Act
        sut.replaceCourse(course);

        // Assert
    }

    @Test
    public void removeCourseFromSchedule_worksAsImplied() {
        // Arrange
        User user = new Student("Test", "Test", "Test", "Test", "Test");
        ArrayList<Course> newSched = new ArrayList<Course>();
        Course course = new Course("Intro to Test", "TEST", 101, 1);
        newSched.add(course);
        user.setSchedule(newSched);

        // Act
        User updatedUser = sut.removeCourseFromSchedule(user, course);

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

    @Test
    public void generateSchedule_doesNotThrowException() {
        // Arrange
        Faculty user = new Faculty();
        Faculty user1 = new Faculty("Test", "Test", "Test", "Test", "Test");
        List<Course> courseList = new ArrayList<>();
        when(mockCourseRepo.retrieveInstructorCourses(any(), any())).thenReturn(courseList);

        // Act
        sut.generateSchedule(user);
        sut.generateSchedule(user1);

        // Assert

    }
}
