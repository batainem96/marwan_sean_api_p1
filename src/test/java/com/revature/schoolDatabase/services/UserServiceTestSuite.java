package com.revature.schoolDatabase.services;

import com.revature.schoolDatabase.datasource.models.Faculty;
import com.revature.schoolDatabase.datasource.models.User;
import com.revature.schoolDatabase.datasource.models.Student;
import com.revature.schoolDatabase.datasource.repositories.UserRepository;
import com.revature.schoolDatabase.web.dtos.Principal;
import org.junit.*;
import com.revature.schoolDatabase.util.exceptions.InvalidRequestException;
import com.revature.schoolDatabase.util.exceptions.ResourcePersistenceException;

import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

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
        User validStudent = new Student("valid", "valid", "valid", "valid", "valid");
        User validFaculty = new Faculty("valid", "valid", "valid", "valid", "valid");

        // Act
        boolean actualResult1 = sut.isUserValid(validStudent);
        boolean actualResult2 = sut.isUserValid(validFaculty);

        // Assert
        Assert.assertEquals("Expected user to be considered valid!", expectedResult, actualResult1);
        Assert.assertEquals("Expected user to be considered valid!", expectedResult, actualResult2);
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

    @Test
    public void register_returnsSuccessfully_whenGivenValidUser() {
        // Arrange
        User expectedResult = new Student("1", "valid", "valid", "valid", "valid");
        User validUser = new Student("valid", "valid", "valid", "valid", "valid");
        when(mockUserRepo.save(any())).thenReturn(expectedResult);

        // Act
        User actualResult = sut.register(validUser);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
        verify(mockUserRepo, times(1)).save(any());
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
    public void register_throwsException_whenGivenUserWithDuplicateUsername() {
        // Arrange
        User existingUser = new Student("original", "last", "email", "duplicate", "original");
        User duplicate = new Student("first", "last", "email", "duplicate", "password");
        when(mockUserRepo.findUserByCredentials(duplicate.getUsername())).thenReturn(existingUser);

        // Act
        try {
            sut.register(duplicate);
        } finally {
            // Assert
            verify(mockUserRepo, times(1)).findUserByCredentials(duplicate.getUsername());
            verify(mockUserRepo, times(0)).save(duplicate);
        };
    }

    @Test
    public void login_returnsNull_whenGivenBadCredentials() {
        // Arrange
        String newUsername = "blankblank";
        String newPassword = "password";
        Principal expectedResult1 = null;

        String existingUsername = "sdunn";
        String wrongPassword = "wrongpassword";
        Principal expectedResult2 = null;

        // Act
        Principal actualResult1 = sut.login(newUsername, newPassword);
        Principal actualResult2 = sut.login(existingUsername, wrongPassword);

        // Assert
        Assert.assertEquals("User not found in database.", expectedResult1, actualResult1);
        Assert.assertEquals("Username or password is incorrect.", expectedResult2, actualResult2);
    }

    @Test(expected = ResourcePersistenceException.class)
    public void updateUser_throwsException_ifUpdateUnacknowledged() {
        // Arrange
        when(mockUserRepo.update(any())).thenReturn(false);

        // Act
        sut.updateUser(new Student());

        // Assert
        verify(mockUserRepo, times(1)).update(any());
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
}
