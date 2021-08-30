package com.revature.portal.services;

import com.revature.portal.datasource.models.Faculty;
import com.revature.portal.datasource.models.User;
import com.revature.portal.datasource.models.Student;
import com.revature.portal.datasource.repositories.UserRepository;
import com.revature.portal.web.dtos.Principal;
import com.revature.portal.web.dtos.UserDTO;
import org.junit.*;
import com.revature.portal.util.exceptions.InvalidRequestException;
import com.revature.portal.util.exceptions.ResourcePersistenceException;

import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTestSuite {

    UserService sut; // SUT = System Under Test (the thing being tested)

    private UserRepository mockUserRepo;

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
        mockUserRepo = Mockito.mock(UserRepository.class);
        sut = new UserService(mockUserRepo);
    }

    @After // runs after each test case
    public void afterEachTest() {
        sut = null;
    }

    @Test
    public void isUserValid_returnsTrue_givenValidUser() {
        // AAA - Arrange, Act, Assert

        // Arrange
        boolean expectedResult = true;
        User validStudent = new User("valid", "valid", "valid@email.com", "valid", "valid", "student");

        // Act
        boolean actualResult1 = sut.isUserValid(validStudent);

        // Assert
        Assert.assertEquals("Expected user to be considered valid!", expectedResult, actualResult1);
    }

    @Test
    public void isUserValid_returnsFalse_givenUserWithNullOrEmptyFirstName() {

        // Arrange
        User invalidStudent1 = new Student(null, "valid", "valid", "valid", "valid");
        User invalidStudent2 = new Student("", "valid", "valid", "valid", "valid");
        User invalidStudent3 = new Student("        ", "valid", "valid", "valid", "valid");

        User invalidFaculty1 = new Faculty(null, "valid", "valid", "valid", "valid");
        User invalidFaculty2 = new Faculty("", "valid", "valid", "valid", "valid");
        User invalidFaculty3 = new Faculty("        ", "valid", "valid", "valid", "valid");

        // Act
        boolean actualResult1 = sut.isUserValid(invalidStudent1);
        boolean actualResult2 = sut.isUserValid(invalidStudent2);
        boolean actualResult3 = sut.isUserValid(invalidStudent3);

        boolean actualResult4 = sut.isUserValid(invalidFaculty1);
        boolean actualResult5 = sut.isUserValid(invalidFaculty2);
        boolean actualResult6 = sut.isUserValid(invalidFaculty3);

        // Assert
        Assert.assertFalse("User first name cannot be null!", actualResult1);
        Assert.assertFalse("User first name cannot be an empty string!", actualResult2);
        Assert.assertFalse("User first name cannot be only whitespace!", actualResult3);

        Assert.assertFalse("User first name cannot be null!", actualResult4);
        Assert.assertFalse("User first name cannot be an empty string!", actualResult5);
        Assert.assertFalse("User first name cannot be only whitespace!", actualResult6);
    }

    @Test(expected = InvalidRequestException.class)
    public void register_throwsException_whenGivenInvalidUser() {
        // Arrange
        User invalidUser = new Student(null, "", "", "", "");

        // Act
        try {
            sut.register(invalidUser);
        } finally {
            // Assert
            verify(mockUserRepo, times(0)).save(any());
        }
    }

    @Test(expected = ResourcePersistenceException.class)
    public void deleteUser_throwsException_ifDeleteUnacknowledged() {
        // Arrange
        when(mockUserRepo.deleteById(any())).thenReturn(false);

        // Act
        sut.deleteUser(new Student());

        // Assert
        verify(mockUserRepo, times(1)).deleteById(any());
    }

    @Test
    public void isIsDefinitelyTrue_returnsTrue_whenGivenTrue() {
        boolean actualResult = sut.isTrue(true);

        Assert.assertTrue(actualResult);
    }
}
