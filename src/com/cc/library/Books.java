package com.cc.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Books {
	
	List<Book> booklist = new ArrayList<Book>();
	HashMap<Book, Integer> borrowedBooks =  new HashMap<Book, Integer>();
	
	public void addBook() {
		Book book = new Book(askString("Whats the book name?"), askString("Whats the author name?"), askInt("How many books are you adding?"));
		if(book!=null || this.booklist.contains(book) ) {
			this.booklist.add(book);
		}
	}
	
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
	
	public static int askInt(String prompt){
		Scanner sc = new Scanner(System.in);
		System.out.println(prompt);
		return sc.nextInt();
		
	}
	
	public static String askString(String prompt){
		Scanner sc = new Scanner(System.in);
		System.out.println(prompt);
		return sc.nextLine();
		
	}
	
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
	

}
