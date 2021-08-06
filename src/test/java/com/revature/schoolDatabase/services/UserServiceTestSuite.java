package com.revature.schoolDatabase.services;

import com.revature.schoolDatabase.models.Person;
import com.revature.schoolDatabase.models.Student;
import com.revature.schoolDatabase.repositories.UserRepository;
import org.junit.*;
import com.revature.schoolDatabase.util.exceptions.InvalidRequestException;

import org.mockito.Mockito;

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
        Person validUser = new Student("valid", "valid", "valid", "valid");

        // Act
        boolean actualResult = sut.isUserValid(validUser);

        // Assert
        Assert.assertEquals("Expected user to be considered valid!", expectedResult, actualResult);
    }

    @Test
    public void isUserValid_returnsFalse_givenUserWithNullOrEmptyFirstName() {

        // Arrange
        Person invalidUser1 = new Student(null, "valid", "valid", "valid");
        Person invalidUser2 = new Student("", "valid", "valid", "valid");
        Person invalidUser3 = new Student("        ", "valid", "valid", "valid");

        // Act
        boolean actualResult1 = sut.isUserValid(invalidUser1);
        boolean actualResult2 = sut.isUserValid(invalidUser2);
        boolean actualResult3 = sut.isUserValid(invalidUser3);

        // Assert
        Assert.assertFalse("User first name cannot be null!", actualResult1);
        Assert.assertFalse("User first name cannot be an empty string!", actualResult2);
        Assert.assertFalse("User first name cannot be only whitespace!", actualResult3);

    }

//    @Test
//    public void register_returnsSuccessfully_whenGivenValidUser() {
        // Arrange
//        AppUser expectedResult = new AppUser(1, "valid", "valid", "valid", "valid", "valid");
//        AppUser validUser = new AppUser("valid", "valid", "valid", "valid", "valid");
//        when(mockUserRepo.save(any())).thenReturn(expectedResult);

        // Act
//        AppUser actualResult = sut.register(validUser);

        // Assert
//        Assert.assertEquals(expectedResult, actualResult);
//        Mockito.verify(mockUserRepo, times(1)).save(any());
//    }

//    @Test(expected = InvalidRequestException.class)
//    public void register_throwsException_whenGivenInvalidUser() {
        // Arrange
//        AppUser invalidUser = new AppUser(null, "", "", "", "");

        // Act
//        try {
//            sut.register(invalidUser);
//        } finally {
//            // Assert
//            Mockito.verify(mockUserRepo, times(0)).save(any());
//        }
//    }

//    @Test(expected = ResourcePersistenceException.class)
//    public void register_throwsException_whenGivenUserWithDuplicateUsername() {
//        // Arrange
//        AppUser existingUser = new AppUser("original", "last", "email", "duplicate", "original");
//        AppUser duplicate = new AppUser("first", "last", "email", "duplicate", "password");
//        when(mockUserRepo.findUserByUsername(duplicate.getUsername())).thenReturn(existingUser);
//
//        // Act
//        try {
//            sut.register(duplicate);
//        } finally {
//            // Assert
//            verify(mockUserRepo, times(1)).findUserByUsername(duplicate.getUsername());
//            verify(mockUserRepo, times(0)).save(duplicate);
//        };
//    }
}
