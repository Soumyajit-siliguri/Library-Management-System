package com.cc.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

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
}