package com.cc.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.InputMismatchException;

/**
 * Comprehensive unit tests for the Books class.
 * Testing framework: JUnit 5
 * 
 * This test suite covers all public methods in the Books class including:
 * - addBook(), rentAbook(), returnAbook(), showAllBooks()
 * - searchBook(), updateBook(), askInt(), askString()
 * 
 * Tests include happy paths, edge cases, and error conditions.
 */
public class BooksTest {

    private Books books;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private ByteArrayInputStream inputStream;

    @BeforeEach
    void setUp() {
        books = new Books();
        // Reset the static counter in Book class to ensure predictable IDs
        Book.counter = 0;
        
        // Capture System.out for testing console output
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        if (inputStream != null) {
            inputStream.close();
        }
    }

    private void setSystemInput(String input) {
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
        Books.sc = new Scanner(System.in);
    }

    // ==================== addBook() Tests ====================

    @Test
    void testAddBook_ValidInput_AddsBookToList() {
        setSystemInput("Test Book\nTest Author\n5\n");
        
        books.addBook();
        
        assertEquals(1, books.booklist.size());
        assertEquals("Test Book", books.booklist.get(0).getBookName());
        assertEquals("Test Author", books.booklist.get(0).getBookAuthor());
        assertEquals(5, books.booklist.get(0).getQuantity());
        assertEquals(1, books.booklist.get(0).getBookID());
    }

    @Test
    void testAddBook_EmptyStrings_StillAddsBook() {
        setSystemInput("\n\n1\n");
        
        books.addBook();
        
        assertEquals(1, books.booklist.size());
        assertEquals("", books.booklist.get(0).getBookName());
        assertEquals("", books.booklist.get(0).getBookAuthor());
        assertEquals(1, books.booklist.get(0).getQuantity());
    }

    @Test
    void testAddBook_ZeroQuantity_AddsBookWithZeroQuantity() {
        setSystemInput("Book\nAuthor\n0\n");
        
        books.addBook();
        
        assertEquals(1, books.booklist.size());
        assertEquals(0, books.booklist.get(0).getQuantity());
    }

    @Test
    void testAddBook_NegativeQuantity_AddsBookWithNegativeQuantity() {
        setSystemInput("Book\nAuthor\n-5\n");
        
        books.addBook();
        
        assertEquals(1, books.booklist.size());
        assertEquals(-5, books.booklist.get(0).getQuantity());
    }

    @Test
    void testAddBook_LogicError_AlwaysAddsBook() {
        // Note: The current logic has a bug - the condition should use &&, not ||
        // This test documents the current behavior
        setSystemInput("Book\nAuthor\n5\n");
        
        books.addBook();
        
        assertEquals(1, books.booklist.size());
        // The book is added even though the logic seems flawed
    }

    // ==================== rentAbook() Tests ====================

    @Test
    void testRentAbook_ValidBookID_BorrowsBookSuccessfully() {
        // Setup: Add a book first
        Book testBook = new Book("Test Book", "Test Author", 5);
        books.booklist.add(testBook);
        
        setSystemInput("1\n"); // Book ID 1
        
        books.rentAbook();
        
        assertEquals(4, testBook.getQuantity()); // Quantity reduced
        assertEquals(1, books.borrowedBooks.size()); // Book added to borrowed
        assertTrue(books.borrowedBooks.containsKey(testBook));
        assertEquals(1, books.borrowedBooks.get(testBook).intValue());
        
        String output = outputStream.toString();
        assertTrue(output.contains("has been added to your cart"));
        assertTrue(output.contains("Good Choice. Happy Reading"));
    }

    @Test
    void testRentAbook_InvalidBookID_ShowsErrorMessage() {
        Book testBook = new Book("Test Book", "Test Author", 5);
        books.booklist.add(testBook);
        
        setSystemInput("999\n"); // Non-existent ID
        
        books.rentAbook();
        
        assertEquals(5, testBook.getQuantity()); // Quantity unchanged
        assertEquals(0, books.borrowedBooks.size()); // No books borrowed
        
        String output = outputStream.toString();
        assertTrue(output.contains("Sorry! Input invalid / that Book ID is not available"));
    }

    @Test
    void testRentAbook_BookWithQuantityOne_NotAvailableForBorrowing() {
        Book testBook = new Book("Test Book", "Test Author", 1);
        books.booklist.add(testBook);
        
        setSystemInput("1\n");
        
        books.rentAbook();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Avaliable books to borrow:"));
        // Should not show any books since quantity is 1 (condition checks >1)
        assertFalse(output.contains("Test Book"));
    }

    @Test
    void testRentAbook_EmptyBookList_ShowsNoAvailableBooks() {
        setSystemInput("1\n");
        
        books.rentAbook();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Avaliable books to borrow:"));
    }

    @Test
    void testRentAbook_MultipleValidBooks_ShowsAllAvailable() {
        Book book1 = new Book("Book 1", "Author 1", 3);
        Book book2 = new Book("Book 2", "Author 2", 2);
        Book book3 = new Book("Book 3", "Author 3", 1); // Won't be shown (quantity <=1)
        books.booklist.add(book1);
        books.booklist.add(book2);
        books.booklist.add(book3);
        
        setSystemInput("1\n"); // Borrow book1
        
        books.rentAbook();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Book 1"));
        assertTrue(output.contains("Book 2"));
        assertFalse(output.contains("Book 3")); // Quantity is 1
    }

    @Test
    void testRentAbook_IndexOutOfBounds_ShowsErrorMessage() {
        Book testBook = new Book("Test Book", "Test Author", 5);
        books.booklist.add(testBook);
        
        setSystemInput("0\n"); // Index 0 when book ID is 1
        
        books.rentAbook();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Sorry! Input invalid / that Book ID is not available"));
    }

    // ==================== returnAbook() Tests ====================

    @Test
    void testReturnAbook_ValidReturn_ReturnsBookSuccessfully() {
        // Setup: Add and borrow a book first
        Book testBook = new Book("Test Book", "Test Author", 5);
        books.booklist.add(testBook);
        books.borrowedBooks.put(testBook, 1);
        testBook.setQuantity(4); // Simulate borrowed state
        
        setSystemInput("1\n"); // Book ID 1
        
        books.returnAbook();
        
        assertEquals(5, testBook.getQuantity()); // Quantity restored
        assertFalse(books.borrowedBooks.containsKey(testBook)); // Removed from borrowed
        
        String output = outputStream.toString();
        assertTrue(output.contains("successfully returned"));
    }

    @Test
    void testReturnAbook_MultipleCopiesBorrowed_DecrementsBorrowCount() {
        Book testBook = new Book("Test Book", "Test Author", 5);
        books.booklist.add(testBook);
        books.borrowedBooks.put(testBook, 2); // 2 copies borrowed
        testBook.setQuantity(3);
        
        setSystemInput("1\n");
        
        books.returnAbook();
        
        assertEquals(4, testBook.getQuantity()); // Quantity increased by 1
        assertTrue(books.borrowedBooks.containsKey(testBook)); // Still in borrowed map
        assertEquals(1, books.borrowedBooks.get(testBook).intValue()); // Count decremented
    }

    @Test
    void testReturnAbook_BookNotBorrowed_ShowsErrorMessage() {
        Book testBook = new Book("Test Book", "Test Author", 5);
        books.booklist.add(testBook);
        
        setSystemInput("1\n");
        
        books.returnAbook();
        
        assertEquals(5, testBook.getQuantity()); // Quantity unchanged
        
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid input or This book was never borrowed"));
    }

    @Test
    void testReturnAbook_InvalidBookID_ShowsErrorMessage() {
        Book testBook = new Book("Test Book", "Test Author", 5);
        books.booklist.add(testBook);
        books.borrowedBooks.put(testBook, 1);
        
        setSystemInput("999\n"); // Invalid ID
        
        books.returnAbook();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid input or This book was never borrowed"));
    }

    // ==================== showAllBooks() Tests ====================

    @Test
    void testShowAllBooks_EmptyList_ShowsNoBooks() {
        books.showAllBooks();
        
        String output = outputStream.toString();
        assertTrue(output.contains("No books available in the library"));
    }

    @Test
    void testShowAllBooks_WithBooks_DisplaysAllBooks() {
        Book book1 = new Book("Book 1", "Author 1", 3);
        Book book2 = new Book("Book 2", "Author 2", 2);
        books.booklist.add(book1);
        books.booklist.add(book2);
        
        books.showAllBooks();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Available books in the library:"));
        assertTrue(output.contains("Book 1"));
        assertTrue(output.contains("Book 2"));
        assertTrue(output.contains("Author 1"));
        assertTrue(output.contains("Author 2"));
    }

    @Test
    void testShowAllBooks_SingleBook_DisplaysCorrectly() {
        Book book = new Book("Single Book", "Single Author", 1);
        books.booklist.add(book);
        
        books.showAllBooks();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Available books in the library:"));
        assertTrue(output.contains("Single Book"));
        assertTrue(output.contains("Single Author"));
    }

    // ==================== searchBook() Tests ====================

    @Test
    void testSearchBook_BookFound_DisplaysFoundBooks() {
        Book book1 = new Book("Java Programming", "Author 1", 3);
        Book book2 = new Book("Python Programming", "Author 2", 2);
        books.booklist.add(book1);
        books.booklist.add(book2);
        
        setSystemInput("Java Programming\n");
        
        books.searchBook();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Books found:"));
        assertTrue(output.contains("Java Programming"));
        assertFalse(output.contains("Python Programming"));
    }

    @Test
    void testSearchBook_CaseInsensitive_FindsBook() {
        Book book1 = new Book("Java Programming", "Author 1", 3);
        books.booklist.add(book1);
        
        setSystemInput("java programming\n");
        
        books.searchBook();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Books found:"));
        assertTrue(output.contains("Java Programming"));
    }

    @Test
    void testSearchBook_BookNotFound_ShowsNotFoundMessage() {
        Book book1 = new Book("Java Programming", "Author 1", 3);
        books.booklist.add(book1);
        
        setSystemInput("Non Existent Book\n");
        
        books.searchBook();
        
        String output = outputStream.toString();
        assertTrue(output.contains("No books found with the name: Non Existent Book"));
    }

    @Test
    void testSearchBook_EmptyBookList_ShowsNotFoundMessage() {
        setSystemInput("Any Book\n");
        
        books.searchBook();
        
        String output = outputStream.toString();
        assertTrue(output.contains("No books found with the name: Any Book"));
    }

    @Test
    void testSearchBook_MultipleBooksWithSameName_FindsAll() {
        Book book1 = new Book("Duplicate Name", "Author 1", 3);
        Book book2 = new Book("Duplicate Name", "Author 2", 2);
        Book book3 = new Book("Different Name", "Author 3", 1);
        books.booklist.add(book1);
        books.booklist.add(book2);
        books.booklist.add(book3);
        
        setSystemInput("Duplicate Name\n");
        
        books.searchBook();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Books found:"));
        assertTrue(output.contains("Author 1"));
        assertTrue(output.contains("Author 2"));
        assertFalse(output.contains("Author 3"));
    }

    @Test
    void testSearchBook_EmptyString_ShowsNotFound() {
        Book book1 = new Book("Test Book", "Test Author", 3);
        books.booklist.add(book1);
        
        setSystemInput("\n");
        
        books.searchBook();
        
        String output = outputStream.toString();
        assertTrue(output.contains("No books found with the name: "));
    }

    // ==================== updateBook() Tests ====================

    @Test
    void testUpdateBook_ValidID_UpdatesQuantity() {
        Book testBook = new Book("Test Book", "Test Author", 5);
        books.booklist.add(testBook);
        
        setSystemInput("0\n10\n"); // Index 0, new quantity 10
        
        books.updateBook();
        
        assertEquals(10, testBook.getQuantity());
        String output = outputStream.toString();
        assertTrue(output.contains("10is the new quantity of Book:"));
    }

    @Test
    void testUpdateBook_NegativeID_ShowsErrorMessage() {
        Book testBook = new Book("Test Book", "Test Author", 5);
        books.booklist.add(testBook);
        
        setSystemInput("-1\n");
        
        books.updateBook();
        
        assertEquals(5, testBook.getQuantity()); // Quantity unchanged
        String output = outputStream.toString();
        assertTrue(output.contains("Not a valid Book ID"));
    }

    @Test
    void testUpdateBook_IDTooLarge_ShowsErrorMessage() {
        Book testBook = new Book("Test Book", "Test Author", 5);
        books.booklist.add(testBook);
        
        setSystemInput("999\n");
        
        books.updateBook();
        
        assertEquals(5, testBook.getQuantity()); // Quantity unchanged
        String output = outputStream.toString();
        assertTrue(output.contains("Not a valid Book ID"));
    }

    @Test
    void testUpdateBook_EmptyBookList_ShowsErrorMessage() {
        setSystemInput("0\n");
        
        books.updateBook();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Not a valid Book ID"));
    }

    @Test
    void testUpdateBook_ZeroQuantity_UpdatesToZero() {
        Book testBook = new Book("Test Book", "Test Author", 5);
        books.booklist.add(testBook);
        
        setSystemInput("0\n0\n");
        
        books.updateBook();
        
        assertEquals(0, testBook.getQuantity());
        String output = outputStream.toString();
        assertTrue(output.contains("0is the new quantity of Book:"));
    }

    @Test
    void testUpdateBook_NegativeQuantity_UpdatesToNegative() {
        Book testBook = new Book("Test Book", "Test Author", 5);
        books.booklist.add(testBook);
        
        setSystemInput("0\n-5\n");
        
        books.updateBook();
        
        assertEquals(-5, testBook.getQuantity());
    }

    @Test
    void testUpdateBook_ValidIDAtBoundary_UpdatesSuccessfully() {
        Book testBook = new Book("Test Book", "Test Author", 5);
        books.booklist.add(testBook);
        
        setSystemInput("0\n100\n"); // Index 0 (valid boundary)
        
        books.updateBook();
        
        assertEquals(100, testBook.getQuantity());
    }

    // ==================== askInt() Tests ====================

    @Test
    void testAskInt_ValidInput_ReturnsInteger() {
        setSystemInput("42\n");
        
        int result = Books.askInt("Enter a number:");
        
        assertEquals(42, result);
        String output = outputStream.toString();
        assertTrue(output.contains("Enter a number:"));
    }

    @Test
    void testAskInt_ZeroInput_ReturnsZero() {
        setSystemInput("0\n");
        
        int result = Books.askInt("Enter zero:");
        
        assertEquals(0, result);
    }

    @Test
    void testAskInt_NegativeInput_ReturnsNegative() {
        setSystemInput("-100\n");
        
        int result = Books.askInt("Enter negative:");
        
        assertEquals(-100, result);
    }

    @Test
    void testAskInt_LargeNumber_ReturnsCorrectly() {
        setSystemInput("2147483647\n"); // Integer.MAX_VALUE
        
        int result = Books.askInt("Enter large number:");
        
        assertEquals(2147483647, result);
    }

    // ==================== askString() Tests ====================

    @Test
    void testAskString_ValidInput_ReturnsString() {
        setSystemInput("Test String\n");
        
        String result = Books.askString("Enter a string:");
        
        assertEquals("Test String", result);
        String output = outputStream.toString();
        assertTrue(output.contains("Enter a string:"));
    }

    @Test
    void testAskString_EmptyInput_ReturnsEmptyString() {
        setSystemInput("\n");
        
        String result = Books.askString("Enter a string:");
        
        assertEquals("", result);
    }

    @Test
    void testAskString_StringWithSpaces_ReturnsFullString() {
        setSystemInput("String with multiple spaces\n");
        
        String result = Books.askString("Enter string:");
        
        assertEquals("String with multiple spaces", result);
    }

    @Test
    void testAskString_SpecialCharacters_ReturnsCorrectly() {
        setSystemInput("Special!@#$%^&*()Characters\n");
        
        String result = Books.askString("Enter special:");
        
        assertEquals("Special!@#$%^&*()Characters", result);
    }

    // ==================== Integration Tests ====================

    @Test
    void testComplexScenario_AddBorrowReturn_WorksCorrectly() {
        // Add a book
        setSystemInput("Complex Book\nComplex Author\n3\n");
        books.addBook();
        
        assertEquals(1, books.booklist.size());
        Book addedBook = books.booklist.get(0);
        assertEquals(3, addedBook.getQuantity());
        
        // Reset output stream
        outputStream.reset();
        
        // Borrow the book
        setSystemInput("1\n"); // Book ID 1
        books.rentAbook();
        
        assertEquals(2, addedBook.getQuantity());
        assertEquals(1, books.borrowedBooks.size());
        
        // Reset output stream
        outputStream.reset();
        
        // Return the book
        setSystemInput("1\n");
        books.returnAbook();
        
        assertEquals(3, addedBook.getQuantity());
        assertEquals(0, books.borrowedBooks.size());
    }

    @Test
    void testMultipleBorrowsSameBook_HandlesCorrectly() {
        Book testBook = new Book("Test Book", "Test Author", 10);
        books.booklist.add(testBook);
        
        // Borrow twice
        setSystemInput("1\n");
        books.rentAbook();
        outputStream.reset();
        
        setSystemInput("1\n");
        books.rentAbook();
        
        assertEquals(8, testBook.getQuantity()); // 10 - 2 = 8
        assertEquals(1, books.borrowedBooks.size()); // Only one entry in map
        assertEquals(2, books.borrowedBooks.get(testBook).intValue()); // Count is 2
    }

    @Test
    void testBorrowUntilUnavailable_BehavesCorrectly() {
        Book testBook = new Book("Limited Book", "Author", 2);
        books.booklist.add(testBook);
        
        // Borrow first copy
        setSystemInput("1\n");
        books.rentAbook();
        assertEquals(1, testBook.getQuantity());
        
        // Try to borrow when quantity is 1 (should not be available)
        outputStream.reset();
        setSystemInput("1\n");
        books.rentAbook();
        
        String output = outputStream.toString();
        // Book should not appear in available list since quantity <=1
        assertTrue(output.contains("Avaliable books to borrow:"));
        assertFalse(output.contains("Limited Book"));
    }

    @Test
    void testSearchAndUpdate_Integration() {
        Book book1 = new Book("Search Me", "Author 1", 5);
        Book book2 = new Book("Update Me", "Author 2", 3);
        books.booklist.add(book1);
        books.booklist.add(book2);
        
        // Search for a book
        setSystemInput("Search Me\n");
        books.searchBook();
        String output = outputStream.toString();
        assertTrue(output.contains("Books found:"));
        assertTrue(output.contains("Search Me"));
        
        // Update the second book (index 1)
        outputStream.reset();
        setSystemInput("1\n20\n");
        books.updateBook();
        
        assertEquals(20, book2.getQuantity());
        output = outputStream.toString();
        assertTrue(output.contains("20is the new quantity"));
    }

    // ==================== Edge Cases and Error Conditions ====================

    @Test
    void testRentBook_BoundaryConditions() {
        Book book = new Book("Boundary Book", "Author", 2);
        books.booklist.add(book);
        
        // Book should be available (quantity > 1)
        setSystemInput("1\n");
        books.rentAbook();
        
        assertEquals(1, book.getQuantity());
        assertTrue(books.borrowedBooks.containsKey(book));
        
        // Now quantity is 1, so book should not be available for borrowing
        outputStream.reset();
        setSystemInput("1\n");
        books.rentAbook();
        
        String output = outputStream.toString();
        assertFalse(output.contains("Boundary Book")); // Should not appear in available list
    }

    @Test 
    void testReturnBook_EdgeCaseWithBookCreation() {
        // This tests the problematic logic in returnAbook method
        Book originalBook = new Book("Original", "Author", 5);
        books.booklist.add(originalBook);
        books.borrowedBooks.put(originalBook, 1);
        
        // The method creates a new Book with just ID, which may not equal the original
        setSystemInput("1\n");
        books.returnAbook();
        
        // Due to the way Book equality works (by ID only), this should work
        assertEquals(6, originalBook.getQuantity());
        assertFalse(books.borrowedBooks.containsKey(originalBook));
    }

    // ==================== Additional Comprehensive Tests - Bug Detection ====================
    
    @Test
    void testAddBook_LogicBugWithOrCondition() {
        // BUG: The condition if(book!=null || this.booklist.contains(book)) 
        // uses OR instead of AND, allowing duplicates
        setSystemInput("DuplicateBook\nAuthor\n5\n");
        books.addBook();
        assertEquals(1, books.booklist.size());
        
        // Add same book again - buggy logic allows duplicate
        setSystemInput("DuplicateBook\nAuthor\n5\n");
        books.addBook();
        
        // Bug confirmed: duplicate gets added
        assertEquals(2, books.booklist.size());
    }
    
    @Test
    void testRentAbook_IndexVsIDBug() {
        // BUG: rentAbook uses booklist.get(borrowingBookID) treating ID as index
        Book book1 = new Book("Book1", "Author1", 5);
        Book book2 = new Book("Book2", "Author2", 3);
        books.booklist.add(book1); // index 0, ID will be >0
        books.booklist.add(book2); // index 1, ID will be >1
        
        int actualID = book2.getBookID();
        setSystemInput(actualID + "\n");
        
        try {
            books.rentAbook();
            // Will throw IndexOutOfBoundsException when ID > list size
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected - documents the bug
            assertTrue(true);
        }
    }
    
    @Test
    void testReturnAbook_SameIndexBug() {
        // BUG: returnAbook also uses booklist.get(bookIDtoReturn)
        Book book = new Book("Book", "Author", 5);
        books.booklist.add(book);
        books.borrowedBooks.put(book, 1);
        
        int bookID = book.getBookID();
        setSystemInput(bookID + "\n");
        
        try {
            books.returnAbook();
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected due to ID/index mismatch
            assertTrue(true);
        }
    }
    
    // ==================== Scanner Buffer Management Issues ====================
    
    @Test
    void testScannerNextIntNextLineIssue() {
        // Classic Scanner problem: nextInt() leaves newline in buffer
        setSystemInput("42\nExpectedString\n");
        
        int num = Books.askInt("Number:");
        String str = Books.askString("String:");
        
        assertEquals(42, num);
        // Bug: nextLine() reads the leftover newline, not "ExpectedString"
        assertEquals("", str);
    }
    
    @Test
    void testMultipleAskOperations() {
        setSystemInput("First\nSecond\n100\nThird\n");
        
        String s1 = Books.askString("S1:");
        String s2 = Books.askString("S2:");
        int i1 = Books.askInt("I1:");
        String s3 = Books.askString("S3:");
        
        assertEquals("First", s1);
        assertEquals("Second", s2);
        assertEquals(100, i1);
        assertEquals("", s3); // Scanner buffer issue after nextInt
    }
    
    // ==================== Extreme Input Tests ====================
    
    @Test
    void testAddBook_MassiveInputStrings() {
        // Test with 10,000 character book name
        String longName = "X".repeat(10000);
        String longAuthor = "Y".repeat(5000);
        
        setSystemInput(longName + "\n" + longAuthor + "\n999\n");
        books.addBook();
        
        assertEquals(1, books.booklist.size());
        assertEquals(longName, books.booklist.get(0).getBookName());
        assertEquals(longAuthor, books.booklist.get(0).getBookAuthor());
    }
    
    @Test
    void testAddBook_UnicodeAndEmojis() {
        String unicodeName = "📚 Bücher 本 książka βιβλίο 📖";
        String unicodeAuthor = "👨‍🏫 José García Müller 李明 🌍";
        
        setSystemInput(unicodeName + "\n" + unicodeAuthor + "\n7\n");
        books.addBook();
        
        assertEquals(unicodeName, books.booklist.get(0).getBookName());
        assertEquals(unicodeAuthor, books.booklist.get(0).getBookAuthor());
    }
    
    @Test
    void testAddBook_ControlCharacters() {
        String nameWithTabs = "Book\t\tWith\tTabs";
        String nameWithNewlines = "Book\\nWith\\nNewlines"; // Escaped in input
        
        setSystemInput(nameWithTabs + "\n" + nameWithNewlines + "\n5\n");
        books.addBook();
        
        assertEquals(nameWithTabs, books.booklist.get(0).getBookName());
        assertEquals(nameWithNewlines, books.booklist.get(0).getBookAuthor());
    }
    
    // ==================== Boundary Value Analysis ====================
    
    @Test
    void testRentAbook_QuantityBoundaries() {
        // Test boundary: quantity must be >1 to be available
        Book q0 = new Book("Q0", "A", 0);
        Book q1 = new Book("Q1", "A", 1);
        Book q2 = new Book("Q2", "A", 2);
        
        books.booklist.add(q0);
        books.booklist.add(q1);
        books.booklist.add(q2);
        
        setSystemInput("1\n");
        books.rentAbook();
        
        String output = outputStream.toString();
        assertFalse(output.contains("Q0"));
        assertFalse(output.contains("Q1")); // quantity=1 not available
        assertTrue(output.contains("Q2"));  // quantity=2 is available
    }
    
    @Test
    void testUpdateBook_EdgeIndices() {
        books.booklist.add(new Book("Book", "Author", 5));
        
        // Test index = size (out of bounds)
        setSystemInput("1\n");
        books.updateBook();
        String output = outputStream.toString();
        assertTrue(output.contains("Not a valid Book ID"));
        
        // Test index = -1 (negative)
        outputStream.reset();
        setSystemInput("-1\n");
        books.updateBook();
        output = outputStream.toString();
        assertTrue(output.contains("Not a valid Book ID"));
        
        // Test index = 0 (valid)
        outputStream.reset();
        setSystemInput("0\n100\n");
        books.updateBook();
        assertEquals(100, books.booklist.get(0).getQuantity());
    }
    
    @Test
    void testBorrowedBooks_IntegerLimits() {
        Book book = new Book("MaxBook", "Author", Integer.MAX_VALUE);
        books.booklist.add(book);
        
        // Set borrowed count to MAX_VALUE-1
        books.borrowedBooks.put(book, Integer.MAX_VALUE - 1);
        
        // Try to borrow one more (would overflow)
        setSystemInput("1\n");
        books.rentAbook();
        
        // Check overflow behavior
        int count = books.borrowedBooks.get(book);
        assertEquals(Integer.MAX_VALUE, count); // Incremented to MAX_VALUE
        assertEquals(Integer.MAX_VALUE - 1, book.getQuantity());
    }
    
    // ==================== Output Format Bug Documentation ====================
    
    @Test
    void testOutputTypos() {
        Book book = new Book("TestBook", "Author", 5);
        books.booklist.add(book);
        
        // Test "Avaliable" typo
        setSystemInput("1\n");
        books.rentAbook();
        String output = outputStream.toString();
        assertTrue(output.contains("Avaliable books")); // Typo
        
        // Test missing space in update message
        outputStream.reset();
        setSystemInput("0\n20\n");
        books.updateBook();
        output = outputStream.toString();
        assertTrue(output.contains("20is the new quantity")); // Missing space
    }
    
    // ==================== State Consistency and Invariants ====================
    
    @Test
    void testBookQuantityInvariant() {
        // Invariant: available_quantity + borrowed_count = original_quantity
        Book book = new Book("InvariantBook", "Author", 20);
        books.booklist.add(book);
        int original = 20;
        
        // Borrow 8 copies
        for (int i = 0; i < 8; i++) {
            setSystemInput("1\n");
            books.rentAbook();
            outputStream.reset();
        }
        
        int borrowed = books.borrowedBooks.get(book);
        int available = book.getQuantity();
        assertEquals(original, available + borrowed);
        
        // Return 3 copies
        for (int i = 0; i < 3; i++) {
            setSystemInput("1\n");
            books.returnAbook();
            outputStream.reset();
        }
        
        borrowed = books.borrowedBooks.get(book);
        available = book.getQuantity();
        assertEquals(original, available + borrowed);
    }
    
    @Test
    void testHashMapKeyConsistency() {
        // Book equality is based only on ID
        Book book1 = new Book("Name1", "Author1", 10);
        int id = book1.getBookID();
        
        books.borrowedBooks.put(book1, 5);
        
        // Create different book with same ID
        Book book2 = new Book(id);
        
        // Should be considered same key
        assertTrue(books.borrowedBooks.containsKey(book2));
        assertEquals(5, books.borrowedBooks.get(book2).intValue());
        
        // Modify via book2
        books.borrowedBooks.put(book2, 7);
        assertEquals(7, books.borrowedBooks.get(book1).intValue());
    }
    
    // ==================== Performance and Scalability ====================
    
    @Test
    void testLargeLibrary_10000Books() {
        // Add 10,000 books
        for (int i = 0; i < 10000; i++) {
            books.booklist.add(new Book("Book" + i, "Author" + i, i % 50 + 1));
        }
        
        // Search for last book
        setSystemInput("Book9999\n");
        long start = System.currentTimeMillis();
        books.searchBook();
        long duration = System.currentTimeMillis() - start;
        
        assertTrue(duration < 2000, "Search too slow: " + duration + "ms");
        
        // Show all books (stress test output)
        outputStream.reset();
        start = System.currentTimeMillis();
        books.showAllBooks();
        duration = System.currentTimeMillis() - start;
        
        assertTrue(duration < 5000, "Display too slow: " + duration + "ms");
        String output = outputStream.toString();
        assertTrue(output.contains("Book0"));
        assertTrue(output.contains("Book9999"));
    }
    
    @Test
    void testMassiveBorrowingOperations() {
        Book book = new Book("PopularBook", "Author", 1000);
        books.booklist.add(book);
        
        // Perform 500 borrow operations
        for (int i = 0; i < 500; i++) {
            setSystemInput("1\n");
            books.rentAbook();
            outputStream.reset();
        }
        
        assertEquals(500, book.getQuantity());
        assertEquals(500, books.borrowedBooks.get(book).intValue());
        
        // Return 250
        for (int i = 0; i < 250; i++) {
            setSystemInput("1\n");
            books.returnAbook();
            outputStream.reset();
        }
        
        assertEquals(750, book.getQuantity());
        assertEquals(250, books.borrowedBooks.get(book).intValue());
    }
    
    // ==================== Complex Integration Scenarios ====================
    
    @Test
    void testRealWorldLibraryDay() {
        // Simulate a full day at the library
        
        // Morning: Add new arrivals
        String[] titles = {"Clean Code", "Design Patterns", "Refactoring", "TDD By Example", "Domain Driven Design"};
        String[] authors = {"Martin", "GoF", "Fowler", "Beck", "Evans"};
        int[] quantities = {10, 8, 6, 5, 7};
        
        for (int i = 0; i < titles.length; i++) {
            setSystemInput(titles[i] + "\n" + authors[i] + "\n" + quantities[i] + "\n");
            books.addBook();
            outputStream.reset();
        }
        
        // Verify additions
        assertEquals(5, books.booklist.size());
        
        // Students search for books
        setSystemInput("Clean Code\n");
        books.searchBook();
        assertTrue(outputStream.toString().contains("Clean Code"));
        outputStream.reset();
        
        // Multiple students borrow different books
        for (int i = 1; i <= 3; i++) {
            setSystemInput(i + "\n");
            books.rentAbook();
            outputStream.reset();
        }
        
        assertEquals(3, books.borrowedBooks.size());
        
        // Librarian updates inventory (new shipment)
        setSystemInput("0\n15\n"); // Update first book
        books.updateBook();
        assertEquals(15, books.booklist.get(0).getQuantity());
        outputStream.reset();
        
        // Some returns
        setSystemInput("1\n");
        books.returnAbook();
        outputStream.reset();
        
        // End of day report
        books.showAllBooks();
        String report = outputStream.toString();
        for (String title : titles) {
            assertTrue(report.contains(title));
        }
        
        // Verify state consistency
        assertTrue(books.borrowedBooks.size() >= 0);
        for (Book book : books.booklist) {
            assertTrue(book.getQuantity() >= 0);
        }
    }
    
    @Test
    void testConcurrentUserSimulation() {
        // Simulate multiple users interacting with the system
        Book sharedBook = new Book("Popular Title", "Famous Author", 20);
        books.booklist.add(sharedBook);
        
        // User 1: Borrow
        setSystemInput("1\n");
        books.rentAbook();
        outputStream.reset();
        
        // User 2: Borrow same book
        setSystemInput("1\n");
        books.rentAbook();
        outputStream.reset();
        
        // User 3: Try to search
        setSystemInput("Popular Title\n");
        books.searchBook();
        outputStream.reset();
        
        // User 1: Return
        setSystemInput("1\n");
        books.returnAbook();
        outputStream.reset();
        
        // User 4: Borrow
        setSystemInput("1\n");
        books.rentAbook();
        outputStream.reset();
        
        // Verify final state
        int finalBorrowed = books.borrowedBooks.get(sharedBook);
        int finalQuantity = sharedBook.getQuantity();
        assertEquals(20, finalQuantity + finalBorrowed);
        assertEquals(2, finalBorrowed); // Net: 3 borrows - 1 return
    }
    
    // ==================== Error Recovery and Robustness ====================
    
    @Test
    void testSystemRecoveryAfterErrors() {
        Book book = new Book("TestBook", "Author", 10);
        books.booklist.add(book);
        
        // Series of invalid operations
        setSystemInput("999\n"); // Invalid borrow
        books.rentAbook();
        outputStream.reset();
        
        setSystemInput("-5\n"); // Invalid update
        books.updateBook();
        outputStream.reset();
        
        setSystemInput("abc\n"); // Would cause InputMismatchException
        try {
            books.returnAbook();
        } catch (InputMismatchException e) {
            // Expected
        }
        outputStream.reset();
        
        // System should still function correctly
        setSystemInput("1\n");
        books.rentAbook();
        assertEquals(9, book.getQuantity());
        
        setSystemInput("1\n");
        books.returnAbook();
        assertEquals(10, book.getQuantity());
    }
    
    @Test
    void testStaticCounterOverflow() {
        // Save original counter
        int original = Book.counter;
        
        // Set near maximum
        Book.counter = Integer.MAX_VALUE - 1;
        
        Book book1 = new Book("Book1", "Author", 1);
        assertEquals(Integer.MAX_VALUE, book1.getBookID());
        
        // This will overflow
        Book book2 = new Book("Book2", "Author", 1);
        assertEquals(Integer.MIN_VALUE, book2.getBookID());
        
        // Restore counter
        Book.counter = original;
    }
    
    @Test
    void testEmptyStringInputs() {
        // Test all empty inputs
        setSystemInput("\n\n0\n");
        books.addBook();
        
        assertEquals(1, books.booklist.size());
        Book added = books.booklist.get(0);
        assertEquals("", added.getBookName());
        assertEquals("", added.getBookAuthor());
        assertEquals(0, added.getQuantity());
        
        // Search for empty string
        setSystemInput("\n");
        books.searchBook();
        String output = outputStream.toString();
        assertTrue(output.contains("Books found") || output.contains("No books found"));
    }
}