package com.cc.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Books {
	
	List<Book> booklist = new ArrayList<Book>();
	HashMap<Book, Integer> borrowedBooks =  new HashMap<Book, Integer>();
	static Scanner sc = new Scanner(System.in);
	
	/**
	 * Adds a new book to the library collection based on user input for name, author, and quantity.
	 *
	 * Prompts the user to enter the book's name, author, and quantity, then creates and adds the book to the collection if it is not null or already present.
	 */
	public void addBook() {
		Book book = new Book(askString("Whats the book name?"), askString("Whats the author name?"), askInt("How many books are you adding?"));
		if(book!=null || this.booklist.contains(book) ) {
			this.booklist.add(book);
		}
	}
	
	/**
	 * Allows a user to borrow a book by selecting from available books with quantity greater than one.
	 *
	 * Prompts the user to enter the Book ID of a book to borrow. If the ID is valid and the book is available, the book is added to the borrowed books list and its quantity is decreased by one. If the input is invalid or the book is not available, an error message is displayed.
	 */
	public void rentAbook() {
		System.out.println("Avaliable books to borrow:");
		List<Integer> availableIDs = new ArrayList<Integer>();
		for(Book book:booklist) {
			if(book.getQuantity()>1) {
				System.out.println(book);
				availableIDs.add(book.getBookID());
			}
		}
		
		
		int borrowingBookID = askInt("Provide the Book ID of the book, you want to borrow:");
		if(!availableIDs.contains(borrowingBookID)) {
			System.out.println("Sorry! Input invalid / that Book ID is not available");
		}else {
			Book booktobeborrowed = booklist.get(borrowingBookID);
			System.out.println(booktobeborrowed.getBookName()+" has been added to your cart.\nGood Choice. Happy Reading.");
			borrowedBooks.put(booktobeborrowed, borrowedBooks.getOrDefault(booktobeborrowed, 0)+1);
			booktobeborrowed.setQuantity(booktobeborrowed.getQuantity()-1);
		}
	}
	
	/**
	 * Prompts the user with the specified message and reads an integer input from the console.
	 *
	 * @param prompt the message displayed to the user before input
	 * @return the integer value entered by the user
	 */
	public static int askInt(String prompt){
		System.out.println(prompt);
		return sc.nextInt();
		
	}
	
	/**
	 * Prompts the user with the specified message and returns the entered string.
	 *
	 * @param prompt the message displayed to the user before input
	 * @return the string input provided by the user
	 */
	public static String askString(String prompt){
		System.out.println(prompt);
		return sc.nextLine();
		
	}
	
	/**
	 * Processes the return of a borrowed book by its ID.
	 *
	 * Prompts the user to enter the ID of the book to return. If the book is found in the borrowed books list, it updates the borrowed count or removes the entry if all copies are returned, increments the book's available quantity, and confirms the return. If the book was not borrowed or the input is invalid, it notifies the user.
	 */
	public void returnAbook(){
		int bookIDtoReturn = askInt("Which Book ID are you returning?");
		Book findBook = new Book(bookIDtoReturn);
		if(borrowedBooks.keySet().contains(findBook)) {
			if(borrowedBooks.get(findBook)>1) {
				borrowedBooks.put(findBook, borrowedBooks.get(findBook)-1);
			}else {
				borrowedBooks.remove(findBook);
			}
			Book returnedBook = booklist.get(bookIDtoReturn);
			returnedBook.setQuantity(returnedBook.getQuantity()+1);
			System.out.println(returnedBook.getBookName()+" successfully returned");
		}else {
			System.out.println("Invalid input or This book was never borrowed");
		}
		
	}
	
	
	/**
	 * Displays all books currently available in the library.
	 *
	 * Prints a list of all books in the collection, or a message if no books are available.
	 */
	public void showAllBooks() {
		if(booklist.isEmpty()) {
			System.out.println("No books available in the library");
		}else {
			System.out.println("Available books in the library:");
			for(Book book:booklist) {
				System.out.println(book);
			}
		}
	}
	
	/**
	 * Searches for books in the collection by name and displays matching results.
	 *
	 * Prompts the user to enter a book name, performs a case-insensitive search in the book list, and prints all books that match the provided name. If no matches are found, informs the user accordingly.
	 */
	public void searchBook() {
		String bookName = askString("Enter the book name you want to search:");
		List<Book> foundBooks = new ArrayList<Book>();
		for(Book book:booklist) {
			if(book.getBookName().equalsIgnoreCase(bookName)) {
				foundBooks.add(book);
			}
		}
		
		if(foundBooks.isEmpty()) {
			System.out.println("No books found with the name: " + bookName);
		}else {
			System.out.println("Books found:");
			for(Book book:foundBooks) {
				System.out.println(book);
			}
		}
	}
	
	/**
	 * Updates the quantity of a book in the collection based on user input.
	 *
	 * Prompts the user to enter a book ID and a new quantity. If the book ID is valid, updates the specified book's quantity and displays the updated information. If the ID is invalid, notifies the user.
	 */
	public void updateBook() {
		int idofBook = askInt("Which book ID , you want to update?");
		if(idofBook<0 || idofBook>=booklist.size()) {
			System.out.println("Not a valid Book ID");
		}else {
			int newQuantity = askInt("Whats new quantity of book?");
			
			Book bookToUpdate = booklist.get(idofBook);
			bookToUpdate.setQuantity(newQuantity);
			System.out.println(booklist.get(idofBook).getQuantity() + "is the new quantity of Book: "+booklist.get(idofBook).getBookID());
		}
		
	}
	

}
