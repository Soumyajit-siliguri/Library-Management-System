package com.cc.library;

import org.junit.jupiter.api.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Students class.
 * Testing framework: JUnit 5 (Jupiter)
 * 
 * This test suite covers:
 * - registerStudent() method with various inputs
 * - askString() static method with edge cases
 * - searchStudent() method with valid and invalid IDs
 * - showAllStudents() method with empty and populated lists
 * - Integration scenarios combining multiple operations
 * - Edge cases and error conditions
 */
public class StudentsTest {
    
    private Students students;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private final ByteArrayInputStream originalIn = new ByteArrayInputStream(new byte[0]);
    
    @BeforeEach
    void setUp() {
        // Reset Student counter for consistent test results
        Student.counter = 0;
        
        students = new Students();
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        Students.sc = new Scanner(System.in);
    }
    
    @Nested
    @DisplayName("registerStudent() method tests")
    class RegisterStudentTests {
        
        @Test
        @DisplayName("Should successfully register a student with valid name")
        void testRegisterStudentWithValidName() {
            // Arrange
            String input = "John Doe\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            students.registerStudent();
            
            // Assert
            assertEquals(1, students.studentList.size());
            Student registeredStudent = students.studentList.get(0);
            assertNotNull(registeredStudent);
            assertEquals("John Doe", registeredStudent.getStudentName());
            assertEquals(1, registeredStudent.getStudentID());
            
            String output = outputStream.toString();
            assertTrue(output.contains("Whats your name?"));
            assertTrue(output.contains("Student registered successfully."));
        }
        
        @Test
        @DisplayName("Should register multiple students with unique IDs")
        void testRegisterMultipleStudents() {
            // Arrange
            String input = "Alice Smith\nBob Johnson\nCharlie Brown\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            students.registerStudent();
            students.registerStudent();
            students.registerStudent();
            
            // Assert
            assertEquals(3, students.studentList.size());
            
            assertEquals("Alice Smith", students.studentList.get(0).getStudentName());
            assertEquals(1, students.studentList.get(0).getStudentID());
            
            assertEquals("Bob Johnson", students.studentList.get(1).getStudentName());
            assertEquals(2, students.studentList.get(1).getStudentID());
            
            assertEquals("Charlie Brown", students.studentList.get(2).getStudentName());
            assertEquals(3, students.studentList.get(2).getStudentID());
        }
        
        @Test
        @DisplayName("Should handle empty name input")
        void testRegisterStudentWithEmptyName() {
            // Arrange
            String input = "\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            students.registerStudent();
            
            // Assert
            assertEquals(1, students.studentList.size());
            Student registeredStudent = students.studentList.get(0);
            assertNotNull(registeredStudent);
            assertEquals("", registeredStudent.getStudentName());
        }
        
        @Test
        @DisplayName("Should handle special characters in name")
        void testRegisterStudentWithSpecialCharacters() {
            // Arrange
            String specialName = "José María O'Connor-Smith III @#$%";
            String input = specialName + "\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            students.registerStudent();
            
            // Assert
            assertEquals(1, students.studentList.size());
            assertEquals(specialName, students.studentList.get(0).getStudentName());
        }
        
        @Test
        @DisplayName("Should handle very long names")
        void testRegisterStudentWithLongName() {
            // Arrange
            String longName = "A".repeat(1000) + " " + "B".repeat(1000);
            String input = longName + "\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            students.registerStudent();
            
            // Assert
            assertEquals(1, students.studentList.size());
            assertEquals(longName, students.studentList.get(0).getStudentName());
        }
        
        @Test
        @DisplayName("Should handle whitespace-only names")
        void testRegisterStudentWithWhitespace() {
            // Arrange
            String input = "   \t\t   \n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            students.registerStudent();
            
            // Assert
            assertEquals(1, students.studentList.size());
            assertEquals("   \t\t   ", students.studentList.get(0).getStudentName());
        }
        
        @Test
        @DisplayName("Should handle numeric names")
        void testRegisterStudentWithNumericName() {
            // Arrange
            String input = "12345\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            students.registerStudent();
            
            // Assert
            assertEquals(1, students.studentList.size());
            assertEquals("12345", students.studentList.get(0).getStudentName());
        }
    }
    
    @Nested
    @DisplayName("askString() static method tests")
    class AskStringTests {
        
        @Test
        @DisplayName("Should return user input for given prompt")
        void testAskStringReturnsInput() {
            // Arrange
            String expectedInput = "Test Input";
            String input = expectedInput + "\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            String result = Students.askString("Enter something:");
            
            // Assert
            assertEquals(expectedInput, result);
            assertTrue(outputStream.toString().contains("Enter something:"));
        }
        
        @Test
        @DisplayName("Should handle empty input")
        void testAskStringEmptyInput() {
            // Arrange
            String input = "\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            String result = Students.askString("Enter text:");
            
            // Assert
            assertEquals("", result);
            assertTrue(outputStream.toString().contains("Enter text:"));
        }
        
        @Test
        @DisplayName("Should handle multi-line input and return first line")
        void testAskStringMultiLineInput() {
            // Arrange
            String input = "First Line\nSecond Line\nThird Line\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            String result = Students.askString("Enter:");
            
            // Assert
            assertEquals("First Line", result);
        }
        
        @Test
        @DisplayName("Should handle null prompt")
        void testAskStringNullPrompt() {
            // Arrange
            String input = "Response\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            String result = Students.askString(null);
            
            // Assert
            assertEquals("Response", result);
            assertTrue(outputStream.toString().contains("null"));
        }
        
        @Test
        @DisplayName("Should handle empty prompt")
        void testAskStringEmptyPrompt() {
            // Arrange
            String input = "User Input\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            String result = Students.askString("");
            
            // Assert
            assertEquals("User Input", result);
            assertTrue(outputStream.toString().contains(""));
        }
        
        @Test
        @DisplayName("Should handle Unicode characters in input")
        void testAskStringUnicodeInput() {
            // Arrange
            String unicodeInput = "こんにちは 世界 🌍 Ñoño";
            String input = unicodeInput + "\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            String result = Students.askString("Enter greeting:");
            
            // Assert
            assertEquals(unicodeInput, result);
        }
    }
    
    @Nested
    @DisplayName("searchStudent() method tests")
    class SearchStudentTests {
        
        @BeforeEach
        void setUpStudents() {
            // Register some students for testing
            String input = "Alice\nBob\nCharlie\nDavid\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            students.registerStudent();
            students.registerStudent();
            students.registerStudent();
            students.registerStudent();
            
            outputStream.reset(); // Clear setup output
        }
        
        @Test
        @DisplayName("Should find existing student by ID")
        void testSearchStudentFound() {
            // Act
            Student found = students.searchStudent(1);
            
            // Assert
            assertNotNull(found);
            assertEquals("Alice", found.getStudentName());
            assertEquals(1, found.getStudentID());
            assertFalse(outputStream.toString().contains("No student found"));
        }
        
        @Test
        @DisplayName("Should find student with middle ID")
        void testSearchStudentMiddleId() {
            // Act
            Student found = students.searchStudent(3);
            
            // Assert
            assertNotNull(found);
            assertEquals("Charlie", found.getStudentName());
            assertEquals(3, found.getStudentID());
        }
        
        @Test
        @DisplayName("Should find last student by ID")
        void testSearchStudentLastId() {
            // Act
            Student found = students.searchStudent(4);
            
            // Assert
            assertNotNull(found);
            assertEquals("David", found.getStudentName());
            assertEquals(4, found.getStudentID());
        }
        
        @Test
        @DisplayName("Should return null for non-existent student ID")
        void testSearchStudentNotFound() {
            // Act
            Student found = students.searchStudent(99999);
            
            // Assert
            assertNull(found);
            assertTrue(outputStream.toString().contains("No student found with ID: 99999"));
        }
        
        @Test
        @DisplayName("Should handle negative student ID")
        void testSearchStudentNegativeId() {
            // Act
            Student found = students.searchStudent(-1);
            
            // Assert
            assertNull(found);
            assertTrue(outputStream.toString().contains("No student found with ID: -1"));
        }
        
        @Test
        @DisplayName("Should handle zero student ID")
        void testSearchStudentZeroId() {
            // Act
            Student found = students.searchStudent(0);
            
            // Assert
            assertNull(found);
            assertTrue(outputStream.toString().contains("No student found with ID: 0"));
        }
        
        @Test
        @DisplayName("Should search in empty list")
        void testSearchStudentEmptyList() {
            // Arrange
            Students emptyStudents = new Students();
            
            // Act
            Student found = emptyStudents.searchStudent(1);
            
            // Assert
            assertNull(found);
            assertTrue(outputStream.toString().contains("No student found with ID: 1"));
        }
        
        @Test
        @DisplayName("Should handle Integer.MAX_VALUE as ID")
        void testSearchStudentMaxIntId() {
            // Act
            Student found = students.searchStudent(Integer.MAX_VALUE);
            
            // Assert
            assertNull(found);
            assertTrue(outputStream.toString().contains("No student found with ID: " + Integer.MAX_VALUE));
        }
        
        @Test
        @DisplayName("Should handle Integer.MIN_VALUE as ID")
        void testSearchStudentMinIntId() {
            // Act
            Student found = students.searchStudent(Integer.MIN_VALUE);
            
            // Assert
            assertNull(found);
            assertTrue(outputStream.toString().contains("No student found with ID: " + Integer.MIN_VALUE));
        }
        
        @Test
        @DisplayName("Should return same object reference for multiple searches")
        void testSearchStudentReturnsSameReference() {
            // Act
            Student found1 = students.searchStudent(2);
            Student found2 = students.searchStudent(2);
            
            // Assert
            assertNotNull(found1);
            assertNotNull(found2);
            assertSame(found1, found2);
        }
    }
    
    @Nested
    @DisplayName("showAllStudents() method tests")
    class ShowAllStudentsTests {
        
        @Test
        @DisplayName("Should display message when no students registered")
        void testShowAllStudentsEmpty() {
            // Act
            students.showAllStudents();
            
            // Assert
            String output = outputStream.toString();
            assertTrue(output.contains("No students registered yet."));
            assertFalse(output.contains("Registered Students:"));
        }
        
        @Test
        @DisplayName("Should display single student")
        void testShowAllStudentsSingle() {
            // Arrange
            String input = "John Doe\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            students.registerStudent();
            outputStream.reset();
            
            // Act
            students.showAllStudents();
            
            // Assert
            String output = outputStream.toString();
            assertTrue(output.contains("Registered Students:"));
            assertTrue(output.contains("Student [studentID=1, studentName=John Doe]"));
            assertFalse(output.contains("No students registered yet."));
        }
        
        @Test
        @DisplayName("Should display multiple students in order")
        void testShowAllStudentsMultiple() {
            // Arrange
            String input = "Alice\nBob\nCharlie\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            students.registerStudent();
            students.registerStudent();
            students.registerStudent();
            outputStream.reset();
            
            // Act
            students.showAllStudents();
            
            // Assert
            String output = outputStream.toString();
            assertTrue(output.contains("Registered Students:"));
            assertTrue(output.contains("Student [studentID=1, studentName=Alice]"));
            assertTrue(output.contains("Student [studentID=2, studentName=Bob]"));
            assertTrue(output.contains("Student [studentID=3, studentName=Charlie]"));
            
            // Verify order
            int aliceIndex = output.indexOf("Alice");
            int bobIndex = output.indexOf("Bob");
            int charlieIndex = output.indexOf("Charlie");
            assertTrue(aliceIndex < bobIndex);
            assertTrue(bobIndex < charlieIndex);
        }
        
        @Test
        @DisplayName("Should handle large number of students")
        void testShowAllStudentsLargeList() {
            // Arrange
            StringBuilder inputBuilder = new StringBuilder();
            for (int i = 1; i <= 100; i++) {
                inputBuilder.append("Student").append(i).append("\n");
            }
            System.setIn(new ByteArrayInputStream(inputBuilder.toString().getBytes()));
            Students.sc = new Scanner(System.in);
            
            for (int i = 0; i < 100; i++) {
                students.registerStudent();
            }
            outputStream.reset();
            
            // Act
            students.showAllStudents();
            
            // Assert
            String output = outputStream.toString();
            assertTrue(output.contains("Registered Students:"));
            assertTrue(output.contains("Student [studentID=1, studentName=Student1]"));
            assertTrue(output.contains("Student [studentID=50, studentName=Student50]"));
            assertTrue(output.contains("Student [studentID=100, studentName=Student100]"));
        }
        
        @Test
        @DisplayName("Should display students with special characters correctly")
        void testShowAllStudentsSpecialCharacters() {
            // Arrange
            String input = "José María\n北京\n@#$%^&*()\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            students.registerStudent();
            students.registerStudent();
            students.registerStudent();
            outputStream.reset();
            
            // Act
            students.showAllStudents();
            
            // Assert
            String output = outputStream.toString();
            assertTrue(output.contains("José María"));
            assertTrue(output.contains("北京"));
            assertTrue(output.contains("@#$%^&*()"));
        }
    }
    
    @Nested
    @DisplayName("Integration and workflow tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should handle complete workflow: register, search, display")
        void testCompleteWorkflow() {
            // Arrange
            String input = "Alice Smith\nBob Jones\nCharlie Brown\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act - Register students
            students.registerStudent();
            students.registerStudent();
            students.registerStudent();
            
            // Search for students
            Student found1 = students.searchStudent(1);
            Student found2 = students.searchStudent(2);
            Student notFound = students.searchStudent(999);
            
            // Display all students
            students.showAllStudents();
            
            // Assert
            assertEquals(3, students.studentList.size());
            assertNotNull(found1);
            assertEquals("Alice Smith", found1.getStudentName());
            assertNotNull(found2);
            assertEquals("Bob Jones", found2.getStudentName());
            assertNull(notFound);
            
            String output = outputStream.toString();
            assertTrue(output.contains("Student registered successfully."));
            assertTrue(output.contains("No student found with ID: 999"));
            assertTrue(output.contains("Registered Students:"));
        }
        
        @Test
        @DisplayName("Should handle interleaved operations")
        void testInterleavedOperations() {
            // Arrange
            String input = "Student1\nStudent2\nStudent3\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            students.registerStudent();
            students.showAllStudents();
            
            students.registerStudent();
            Student found = students.searchStudent(1);
            
            students.registerStudent();
            students.showAllStudents();
            
            // Assert
            assertEquals(3, students.studentList.size());
            assertNotNull(found);
            assertEquals("Student1", found.getStudentName());
        }
        
        @Test
        @DisplayName("Should maintain data consistency across operations")
        void testDataConsistency() {
            // Arrange
            String input = "Alice\nBob\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            students.registerStudent();
            Student alice1 = students.searchStudent(1);
            
            students.registerStudent();
            Student alice2 = students.searchStudent(1);
            Student bob = students.searchStudent(2);
            
            // Assert
            assertSame(alice1, alice2);
            assertNotNull(bob);
            assertEquals("Bob", bob.getStudentName());
            assertEquals(2, students.studentList.size());
        }
    }
    
    @Nested
    @DisplayName("Edge cases and boundary conditions")
    class EdgeCasesTests {
        
        @Test
        @DisplayName("Should handle rapid registration of many students")
        void testRapidRegistration() {
            // Arrange
            StringBuilder inputBuilder = new StringBuilder();
            for (int i = 0; i < 50; i++) {
                inputBuilder.append("S").append(i).append("\n");
            }
            System.setIn(new ByteArrayInputStream(inputBuilder.toString().getBytes()));
            Students.sc = new Scanner(System.in);
            
            // Act
            for (int i = 0; i < 50; i++) {
                students.registerStudent();
            }
            
            // Assert
            assertEquals(50, students.studentList.size());
            for (int i = 0; i < 50; i++) {
                Student s = students.searchStudent(i + 1);
                assertNotNull(s);
                assertEquals("S" + i, s.getStudentName());
            }
        }
        
        @Test
        @DisplayName("Should handle search patterns systematically")
        void testSystematicSearch() {
            // Arrange
            String input = "A\nB\nC\nD\nE\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            
            for (int i = 0; i < 5; i++) {
                students.registerStudent();
            }
            
            // Act & Assert - Search in different patterns
            // Forward search
            for (int i = 1; i <= 5; i++) {
                assertNotNull(students.searchStudent(i));
            }
            
            // Backward search
            for (int i = 5; i >= 1; i--) {
                assertNotNull(students.searchStudent(i));
            }
            
            // Random access
            assertNotNull(students.searchStudent(3));
            assertNotNull(students.searchStudent(1));
            assertNotNull(students.searchStudent(5));
            assertNotNull(students.searchStudent(2));
            assertNotNull(students.searchStudent(4));
        }
        
        @Test
        @DisplayName("Should handle duplicate searches efficiently")
        void testDuplicateSearches() {
            // Arrange
            String input = "TestStudent\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            Students.sc = new Scanner(System.in);
            students.registerStudent();
            
            // Act
            Student found1 = students.searchStudent(1);
            Student found2 = students.searchStudent(1);
            Student found3 = students.searchStudent(1);
            Student found4 = students.searchStudent(1);
            Student found5 = students.searchStudent(1);
            
            // Assert - All should be the same reference
            assertNotNull(found1);
            assertSame(found1, found2);
            assertSame(found2, found3);
            assertSame(found3, found4);
            assertSame(found4, found5);
        }
        
        @Test
        @DisplayName("Should verify list is properly initialized")
        void testListInitialization() {
            // Assert
            assertNotNull(students.studentList);
            assertTrue(students.studentList.isEmpty());
            assertEquals(0, students.studentList.size());
        }
        
        @Test
        @DisplayName("Should verify Scanner is properly initialized")
        void testScannerInitialization() {
            // Assert
            assertNotNull(Students.sc);
        }
    }
}