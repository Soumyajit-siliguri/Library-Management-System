package com.cc.library;

import org.junit.jupiter.api.*;
import java.io.*;
import java.util.NoSuchElementException;
import java.util.InputMismatchException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for the Library class.
 * Testing framework: JUnit 5 (Jupiter)
 * 
 * These tests cover:
 * - Menu display functionality
 * - All menu options (0-8)
 * - Edge cases and invalid inputs
 * - System output verification
 * - Proper exit behavior
 * - Error handling and recovery
 */
public class LibraryTest {
    
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private InputStream originalIn;
    
    @BeforeEach
    void setUp() {
        // Capture System.out for output verification
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        // Store original System.in
        originalIn = System.in;
    }
    
    @AfterEach
    void tearDown() {
        // Restore original System.out and System.in
        System.setOut(originalOut);
        System.setIn(originalIn);
    }
    
    // ==================== Welcome and Menu Display Tests ====================
    
    /**
     * Test the welcome message display
     */
    @Test
    @DisplayName("Should display welcome message when library starts")
    void testWelcomeMessage() {
        // Arrange
        String input = "0\n"; // Exit immediately
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Welcome to the GFG Library!"), 
                   "Should display welcome message");
        assertTrue(output.contains("Select From The Following Options:"),
                   "Should display selection prompt");
    }
    
    /**
     * Test menu display functionality
     */
    @Test
    @DisplayName("Should display all menu options correctly")
    void testMenuDisplay() {
        // Arrange
        String input = "0\n"; // Exit immediately
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("1. Add Book"), "Should display Add Book option");
        assertTrue(output.contains("2. Update Book"), "Should display Update Book option");
        assertTrue(output.contains("3. Search Book"), "Should display Search Book option");
        assertTrue(output.contains("4. Show All Books"), "Should display Show All Books option");
        assertTrue(output.contains("5. Register Student"), "Should display Register Student option");
        assertTrue(output.contains("6. Show All Students"), "Should display Show All Students option");
        assertTrue(output.contains("7. Rent a Book"), "Should display Rent a Book option");
        assertTrue(output.contains("8. Return a Book"), "Should display Return a Book option");
        assertTrue(output.contains("0. Exit"), "Should display Exit option");
        assertTrue(output.contains("Enter your choice:"), "Should display choice prompt");
    }
    
    /**
     * Test menu separator display
     */
    @Test
    @DisplayName("Should display menu separators correctly")
    void testMenuSeparators() {
        // Arrange
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("**********************************************************************"),
                   "Should display menu separator lines");
    }
    
    // ==================== Menu Option Tests ====================
    
    /**
     * Test exit functionality (Option 0)
     */
    @Test
    @DisplayName("Should exit gracefully when option 0 is selected")
    void testExitOption() {
        // Arrange
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Thank you for using the library system. Goodbye!"),
                   "Should display goodbye message");
    }
    
    /**
     * Test Add Book functionality (Option 1)
     */
    @Test
    @DisplayName("Should handle add book option without crashing")
    void testAddBookOption() {
        // Arrange - Need to provide complete input for addBook() method
        String input = "1\n" +           // Select Add Book
                      "TestBook\n" +      // Book name
                      "TestAuthor\n" +    // Author name
                      "12345\n" +         // Serial number
                      "10\n" +            // Quantity
                      "0\n";              // Exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> Library.main(new String[]{}),
                          "Add book option should not throw exception");
    }
    
    /**
     * Test Update Book functionality (Option 2)
     */
    @Test
    @DisplayName("Should handle update book option")
    void testUpdateBookOption() {
        // Arrange - Provide input for update book flow
        String input = "2\n" +      // Select Update Book
                      "1\n" +       // Search by serial number
                      "12345\n" +   // Serial number to search
                      "0\n";        // Exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert
        assertDoesNotThrow(() -> Library.main(new String[]{}),
                          "Update book option should not throw exception");
    }
    
    /**
     * Test Search Book functionality (Option 3)
     */
    @Test
    @DisplayName("Should handle search book option")
    void testSearchBookOption() {
        // Arrange
        String input = "3\n" +      // Select Search Book
                      "1\n" +       // Search by serial number
                      "12345\n" +   // Serial number
                      "0\n";        // Exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert
        assertDoesNotThrow(() -> Library.main(new String[]{}),
                          "Search book option should not throw exception");
    }
    
    /**
     * Test Show All Books functionality (Option 4)
     */
    @Test
    @DisplayName("Should handle show all books option")
    void testShowAllBooksOption() {
        // Arrange
        String input = "4\n0\n"; // Select option 4, then exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert
        assertDoesNotThrow(() -> Library.main(new String[]{}),
                          "Show all books option should not throw exception");
    }
    
    /**
     * Test Register Student functionality (Option 5)
     */
    @Test
    @DisplayName("Should handle register student option")
    void testRegisterStudentOption() {
        // Arrange - Provide input for student registration
        String input = "5\n" +           // Select Register Student
                      "John Doe\n" +      // Student name
                      "S12345\n" +        // Student ID
                      "0\n";              // Exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert
        assertDoesNotThrow(() -> Library.main(new String[]{}),
                          "Register student option should not throw exception");
    }
    
    /**
     * Test Show All Students functionality (Option 6)
     */
    @Test
    @DisplayName("Should handle show all students option")
    void testShowAllStudentsOption() {
        // Arrange
        String input = "6\n0\n"; // Select option 6, then exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert
        assertDoesNotThrow(() -> Library.main(new String[]{}),
                          "Show all students option should not throw exception");
    }
    
    /**
     * Test Rent a Book functionality (Option 7)
     */
    @Test
    @DisplayName("Should handle rent a book option")
    void testRentBookOption() {
        // Arrange - Provide input for rent book flow
        String input = "7\n" +         // Select Rent a Book
                      "S12345\n" +      // Student ID
                      "12345\n" +       // Book serial number
                      "0\n";            // Exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert
        assertDoesNotThrow(() -> Library.main(new String[]{}),
                          "Rent a book option should not throw exception");
    }
    
    /**
     * Test Return a Book functionality (Option 8)
     */
    @Test
    @DisplayName("Should handle return a book option")
    void testReturnBookOption() {
        // Arrange - Provide input for return book flow
        String input = "8\n" +         // Select Return a Book
                      "S12345\n" +      // Student ID
                      "12345\n" +       // Book serial number
                      "0\n";            // Exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert
        assertDoesNotThrow(() -> Library.main(new String[]{}),
                          "Return a book option should not throw exception");
    }
    
    // ==================== Edge Cases and Error Handling Tests ====================
    
    /**
     * Test invalid menu option
     */
    @Test
    @DisplayName("Should display error message for invalid menu option")
    void testInvalidMenuOption() {
        // Arrange
        String input = "9\n0\n"; // Invalid option, then exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("ENTER BETWEEN 0 TO 8."),
                   "Should display error message for invalid option");
    }
    
    /**
     * Test negative number input
     */
    @Test
    @DisplayName("Should handle negative number input")
    void testNegativeNumberInput() {
        // Arrange
        String input = "-1\n0\n"; // Negative number, then exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("ENTER BETWEEN 0 TO 8."),
                   "Should display error message for negative input");
    }
    
    /**
     * Test large number input
     */
    @Test
    @DisplayName("Should handle large number input")
    void testLargeNumberInput() {
        // Arrange
        String input = "100\n0\n"; // Large number, then exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("ENTER BETWEEN 0 TO 8."),
                   "Should display error message for large number");
    }
    
    /**
     * Test very large number input (Integer.MAX_VALUE)
     */
    @Test
    @DisplayName("Should handle Integer.MAX_VALUE input")
    void testMaxIntegerInput() {
        // Arrange
        String input = Integer.MAX_VALUE + "\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("ENTER BETWEEN 0 TO 8."),
                   "Should display error message for MAX_VALUE");
    }
    
    /**
     * Test non-numeric input handling
     */
    @Test
    @DisplayName("Should handle non-numeric input with exception")
    void testNonNumericInput() {
        // Arrange
        String input = "abc\n"; // Non-numeric input
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert - Should throw InputMismatchException
        assertThrows(InputMismatchException.class, () -> {
            Library.main(new String[]{});
        }, "Non-numeric input should throw InputMismatchException");
    }
    
    /**
     * Test special characters input
     */
    @Test
    @DisplayName("Should handle special characters input")
    void testSpecialCharactersInput() {
        // Arrange
        String input = "@#$%\n"; // Special characters
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert
        assertThrows(InputMismatchException.class, () -> {
            Library.main(new String[]{});
        }, "Special characters should throw InputMismatchException");
    }
    
    // ==================== Boundary Value Tests ====================
    
    /**
     * Test boundary value: minimum valid option (0)
     */
    @Test
    @DisplayName("Should accept minimum valid option (0)")
    void testMinimumValidOption() {
        // Arrange
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Thank you for using the library system. Goodbye!"),
                   "Option 0 should exit with goodbye message");
    }
    
    /**
     * Test boundary value: maximum valid option (8)
     */
    @Test
    @DisplayName("Should accept maximum valid option (8)")
    void testMaximumValidOption() {
        // Arrange
        String input = "8\n" +         // Select Return a Book (max valid option)
                      "S12345\n" +      // Student ID
                      "12345\n" +       // Book serial number
                      "0\n";            // Exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert
        assertDoesNotThrow(() -> Library.main(new String[]{}),
                          "Maximum valid option (8) should not throw exception");
    }
    
    /**
     * Test just below minimum boundary (-1)
     */
    @Test
    @DisplayName("Should reject option just below minimum boundary")
    void testBelowMinimumBoundary() {
        // Arrange
        String input = "-1\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("ENTER BETWEEN 0 TO 8."),
                   "Should reject -1 as invalid");
    }
    
    /**
     * Test just above maximum boundary (9)
     */
    @Test
    @DisplayName("Should reject option just above maximum boundary")
    void testAboveMaximumBoundary() {
        // Arrange
        String input = "9\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("ENTER BETWEEN 0 TO 8."),
                   "Should reject 9 as invalid");
    }
    
    // ==================== Sequence and Flow Tests ====================
    
    /**
     * Test multiple operations in sequence
     */
    @Test
    @DisplayName("Should handle multiple operations in sequence")
    void testMultipleOperations() {
        // Arrange - Multiple display operations then exit
        String input = "4\n6\n4\n0\n"; 
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert
        assertDoesNotThrow(() -> Library.main(new String[]{}),
                          "Multiple operations should not throw exception");
        
        // Verify menu is displayed multiple times
        String output = outputStream.toString();
        int menuCount = countOccurrences(output, "Enter your choice:");
        assertTrue(menuCount >= 4, "Menu should be displayed at least 4 times");
    }
    
    /**
     * Test menu loop continues after valid operation
     */
    @Test
    @DisplayName("Should continue menu loop after valid operation")
    void testMenuLoopContinues() {
        // Arrange
        String input = "4\n4\n0\n"; // Two operations, then exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert - Menu should be displayed multiple times
        String output = outputStream.toString();
        int menuCount = countOccurrences(output, "Enter your choice:");
        assertTrue(menuCount >= 3, "Menu should be displayed at least 3 times");
    }
    
    /**
     * Test all valid options in sequence
     */
    @Test
    @DisplayName("Should handle all valid options in sequence")
    void testAllValidOptions() {
        // Arrange - Try all display operations
        String input = "4\n6\n4\n6\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert
        assertDoesNotThrow(() -> Library.main(new String[]{}),
                          "All valid options should work");
    }
    
    /**
     * Test rapid successive operations
     */
    @Test
    @DisplayName("Should handle rapid successive display operations")
    void testRapidDisplayOperations() {
        // Arrange - Rapid display operations that don't require input
        String input = "4\n4\n6\n6\n4\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert
        assertDoesNotThrow(() -> Library.main(new String[]{}),
                          "Rapid operations should not throw exception");
    }
    
    /**
     * Test zero option exits immediately
     */
    @Test
    @DisplayName("Should exit immediately with option 0")
    void testZeroExitsImmediately() {
        // Arrange
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert
        String output = outputStream.toString();
        int goodbyeCount = countOccurrences(output, "Thank you for using the library system");
        assertEquals(1, goodbyeCount, "Goodbye message should appear exactly once");
    }
    
    /**
     * Test invalid then valid option sequence
     */
    @Test
    @DisplayName("Should recover from invalid option and accept valid option")
    void testInvalidThenValidOption() {
        // Arrange
        String input = "99\n4\n0\n"; // Invalid, valid, exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("ENTER BETWEEN 0 TO 8."),
                   "Should show error for invalid option");
        assertTrue(output.contains("Thank you for using the library system"),
                   "Should eventually exit properly");
    }
    
    /**
     * Test multiple invalid options
     */
    @Test
    @DisplayName("Should handle multiple invalid options")
    void testMultipleInvalidOptions() {
        // Arrange
        String input = "10\n-5\n999\n0\n"; // Multiple invalid, then exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert
        String output = outputStream.toString();
        int errorCount = countOccurrences(output, "ENTER BETWEEN 0 TO 8.");
        assertEquals(3, errorCount, "Should show error message 3 times for 3 invalid inputs");
    }
    
    /**
     * Test alternating valid and invalid options
     */
    @Test
    @DisplayName("Should handle alternating valid and invalid options")
    void testAlternatingValidInvalid() {
        // Arrange
        String input = "4\n99\n6\n-1\n4\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act
        Library.main(new String[]{});
        
        // Assert
        String output = outputStream.toString();
        int errorCount = countOccurrences(output, "ENTER BETWEEN 0 TO 8.");
        assertEquals(2, errorCount, "Should show error message twice");
        assertTrue(output.contains("Thank you for using the library system"),
                   "Should exit properly");
    }
    
    // ==================== Complete Workflow Tests ====================
    
    /**
     * Test complete book management workflow
     */
    @Test
    @DisplayName("Should handle complete book management workflow")
    void testCompleteBookWorkflow() {
        // Arrange - Add book, search, show all, exit
        String input = "1\n" +              // Add Book
                      "Test Book\n" +        // Book name
                      "Test Author\n" +      // Author name
                      "99999\n" +            // Serial number
                      "5\n" +                // Quantity
                      "3\n" +                // Search Book
                      "1\n" +                // Search by serial
                      "99999\n" +            // Serial number
                      "4\n" +                // Show all books
                      "0\n";                 // Exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert
        assertDoesNotThrow(() -> Library.main(new String[]{}),
                          "Complete book workflow should work");
    }
    
    /**
     * Test complete student management workflow
     */
    @Test
    @DisplayName("Should handle complete student management workflow")
    void testCompleteStudentWorkflow() {
        // Arrange - Register student, show all, exit
        String input = "5\n" +              // Register Student
                      "Jane Smith\n" +       // Student name
                      "S99999\n" +           // Student ID
                      "6\n" +                // Show all students
                      "0\n";                 // Exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert
        assertDoesNotThrow(() -> Library.main(new String[]{}),
                          "Complete student workflow should work");
    }
    
    /**
     * Test library rental workflow
     */
    @Test
    @DisplayName("Should handle library rental workflow")
    void testRentalWorkflow() {
        // Arrange - Add book, register student, rent, return, exit
        String input = "1\n" +              // Add Book
                      "Rental Book\n" +      // Book name
                      "Rental Author\n" +    // Author name
                      "77777\n" +            // Serial number
                      "3\n" +                // Quantity
                      "5\n" +                // Register Student
                      "Rental Student\n" +   // Student name
                      "S77777\n" +           // Student ID
                      "7\n" +                // Rent a Book
                      "S77777\n" +           // Student ID
                      "77777\n" +            // Book serial
                      "8\n" +                // Return a Book
                      "S77777\n" +           // Student ID
                      "77777\n" +            // Book serial
                      "0\n";                 // Exit
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Act & Assert
        assertDoesNotThrow(() -> Library.main(new String[]{}),
                          "Complete rental workflow should work");
    }
    
    // ==================== Helper Methods ====================
    
    /**
     * Helper method to count occurrences of a substring
     */
    private int countOccurrences(String str, String findStr) {
        int count = 0;
        int lastIndex = 0;
        
        while (lastIndex != -1) {
            lastIndex = str.indexOf(findStr, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += findStr.length();
            }
        }
        return count;
    }
}